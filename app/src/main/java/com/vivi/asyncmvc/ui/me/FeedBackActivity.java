package com.vivi.asyncmvc.ui.me;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.studio.view.ViewUtils;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.UrlEntity;
import com.vivi.asyncmvc.base.ActivitySupport;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.comm.view.dialog.ActionSheet;
import com.vivi.asyncmvc.comm.view.edit.CountEditText;
import com.vivi.asyncmvc.ui.comm.selectimg.BrowseImgsActivity;
import com.vivi.asyncmvc.ui.comm.selectimg.MultiImageSelectorActivity;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.CommonTools;
import com.vivi.asyncmvc.library.utils.DataFormat;
import com.vivi.asyncmvc.library.utils.FileUtil;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.comm.adapter.ImageManageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 意见反馈
 *
 * @author gongwei
 * @date 2019/1/30
 */
public class FeedBackActivity extends BaseActivity {

    //views
    @BindView(R.id.edt_feed_back)
    CountEditText edtFeedBack;
    @BindView(R.id.tv_feed_back_image_count)
    TextView tvFeedBackImageCount;
    @BindView(R.id.gd_feed_back_images)
    GridView gdFeedBackImages;
    @BindView(R.id.btn_feed_back)
    Button btnFeedBack;
    //data
    private final int CONTENT_MAX_LENGTH = 200;
    private final int IMAGE_MAX_LENGTH = 4;
    private String mContent;
    private ArrayList<String> mSelectPath = new ArrayList<>();//已选择的图片
    private HashMap<String, String> mSuccessMaps = new LinkedHashMap<>();//上传成功的图片<LocalPath, ServerUrl>
    private HashMap<String, String> mFailMaps = new LinkedHashMap<>();
    //tools
    private ImageManageAdapter mAdapter;
    private boolean isCommitting = false;//是否正在提交，避免重复提交了
    private int imageUploadProgress;//图片上传的进度

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_feed_back;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, FeedBackActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("意见反馈");
        initView();
    }

    private void initView() {
        edtFeedBack.init("输入你要反馈的宝贵意见", CONTENT_MAX_LENGTH);
        edtFeedBack.setEditHeight(ViewUtils.dip2px(this, 160));
        edtFeedBack.addTextChangedListener(new EditTextWatcher(edtFeedBack, new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                mContent = text;
                refreshBtnCommit();
            }
        }));
        refreshBtnCommit();

        mAdapter = new ImageManageAdapter(this, IMAGE_MAX_LENGTH, mSelectPath, new ImageManageAdapter.ImageManageCallBack() {
            @Override
            public void clickImage(int index) {
                BrowseImgsActivity.startForSelected(FeedBackActivity.this, mSelectPath, index);
            }

            @Override
            public void clickDelete(int index) {
                String key = mSelectPath.get(index);
                if (mSuccessMaps.containsKey(key))
                    mSuccessMaps.remove(key);
                if (mFailMaps.containsKey(key))
                    mFailMaps.remove(key);
                mSelectPath.remove(index);
                dataSetChanged();
            }

            @Override
            public void clickAdd() {
                OS.hideSoftKeyboard(FeedBackActivity.this);
                ActivitySupport.requestRunPermission(FeedBackActivity.this, new String[]{Manifest.permission.CAMERA}, new PermissionRequestListener() {
                    @Override
                    public void onGranted() {
                        ActionSheet.create(FeedBackActivity.this)
                                .setCancelable(false)
                                .setCanceledOnTouchOutside(true)
                                .addSheetItem("拍照", new ActionSheet.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int index) {
                                        CommonTools.openCamera(FeedBackActivity.this);
                                    }
                                })
                                .addSheetItem("选取相册", new ActionSheet.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        MultiImageSelectorActivity.startForMulti(FeedBackActivity.this, IMAGE_MAX_LENGTH, mSelectPath);
                                    }
                                }).show();
                    }
                });
            }
        });
        gdFeedBackImages.setAdapter(mAdapter);
    }

    private void refreshBtnCommit() {
        btnFeedBack.setEnabled(!TextUtils.isEmpty(mContent));
    }

    /**
     * 提交开始的检查
     */
    private void commitCheck() {
        if (isCommitting) {
            UI.showToast("正在提交，请稍候...");
            return;
        }
        if (mContent.length() > CONTENT_MAX_LENGTH) {
            UI.showToast(String.format("输入内容不能超过%s字", CONTENT_MAX_LENGTH));
        } else {
            isCommitting = true;
            showLoadingDialog("正在提交");
            imageUploadProgress = 0;
            mFailMaps.clear();
            startImageUpload();
        }
    }

    /**
     * 上传图片
     */
    private void startImageUpload() {
        if ((mSelectPath != null) && !mSelectPath.isEmpty() && imageUploadProgress < mSelectPath.size()) {
            try {
                if (mSuccessMaps.containsKey(mSelectPath.get(imageUploadProgress))) {
                    imageUploadProgress++;
                    startImageUpload();
                } else {
                    uploadImageSingle(mSelectPath.get(imageUploadProgress), new JsonResultCallback<JsonResultVoid>() {

                        @Override
                        public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                            imageUploadProgress++;
                            startImageUpload();
                        }

                        @Override
                        public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                            super.onFailure(statusCode, responseString, throwable, tag);
                            imageUploadProgress++;
                            startImageUpload();
                            UI.showToast(responseString);
                        }
                    });
                }
            } catch (Exception e) {
                dismissLoadingDialog();
                isCommitting = false;
                e.printStackTrace();
            }
        } else {
            if (mFailMaps.size() > 0) {
                dismissLoadingDialog();
                isCommitting = false;
                UI.showConfirmDialog(this, "温馨提示", "图片上传失败，是否重试？", "取消", null, "重试", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        commitCheck();
                    }
                });
            } else {
                commitPublish();
            }
        }
    }

    /**
     * 上传单张图片
     *
     * @param localPath
     * @param callback
     */
    private void uploadImageSingle(final String localPath, final JsonResultCallback<JsonResultVoid> callback) {
        String newPath = FileUtil.saveImageToSDCard(FileUtil.getImage(localPath));
        //遇到那种坏了的图片，localPath对应的图片取出来就是null
        if (TextUtils.isEmpty(newPath)) {
            mFailMaps.put(localPath, "fail");
            callback.onFailure(404, "图片不存在", new Throwable(), 0);
            return;
        }
        File newFile = new File(newPath);
        Api.uploadImage(newFile, new JsonResultCallback<JsonResult<UrlEntity>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<UrlEntity> response, int tag) {
                UrlEntity urlEntity = response.getData();
                if (urlEntity != null) {
                    mSuccessMaps.put(localPath, response.getData().url);
                    mFailMaps.remove(localPath);
                    callback.onSuccess(statusCode, response, tag);
                } else {
                    dismissLoadingDialog();
                    isCommitting = false;
                    callback.onFailure(404, "上传失败", new Throwable(), tag);
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                mFailMaps.put(localPath, "fail");
                callback.onFailure(statusCode, responseString, throwable, tag);
            }
        });
    }

    /**
     * 业务数据提交
     */
    private void commitPublish() {
        String imageUrls = DataFormat.getStringFromMapValue(mSuccessMaps);
        Api.feedBack(mContent, imageUrls, new JsonResultCallback<JsonResultVoid>() {
            @Override
            public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                isCommitting = false;
                dismissLoadingDialog();
                UI.showToast("提交成功");
                finish();
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                isCommitting = false;
                dismissLoadingDialog();
                UI.showToast(responseString);
            }
        });
    }

    private void filterActivityListResult(ArrayList<String> result) {
        //已选照片 和 查看大图回来
        mSelectPath.clear();
        if (result != null && result.size() > 0) {
            mSelectPath.addAll(result);
            //清除success map中已删除的数据
            String[] sKeys = mSuccessMaps.keySet().toArray(new String[0]);
            for (int i = sKeys.length - 1; i >= 0; i--) {
                String key = sKeys[i];
                if (!result.contains(key)) {
                    mSuccessMaps.remove(key);
                }
            }
            //清除fail map中已删除的数据
            String[] fKeys = mFailMaps.keySet().toArray(new String[0]);
            for (int i = fKeys.length - 1; i >= 0; i--) {
                String key = fKeys[i];
                if (!result.contains(key)) {
                    mFailMaps.remove(key);
                }
            }
        } else {
            mSuccessMaps.clear();
            mFailMaps.clear();
        }
        dataSetChanged();
    }

    private void filterActivityListResult(String result) {
        mSelectPath.add(result);
        dataSetChanged();
    }

    private void dataSetChanged() {
        mAdapter.notifyDataSetChanged();
        tvFeedBackImageCount.setText(String.format("%s/%s", mSelectPath.size(), IMAGE_MAX_LENGTH));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppContext.REQUEST_CODE_SELECTED_IMAGES:
                    //选图回来
                    filterActivityListResult(MultiImageSelectorActivity.getImagesFromResultOk(data));
                    break;
                case AppContext.REQUEST_CODE_SELECTED_IMAGES_EDIT:
                    //编辑图回来
                    filterActivityListResult(BrowseImgsActivity.getImagesFromResultOk(data));
                    break;
                case AppContext.REQUEST_CODE_OPEN_CAMERA:
                    //拍照回来
                    String path = CommonTools.getCameraFilePath();
                    if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                        UI.showToast("未找到照片");
                        return;
                    }
                    filterActivityListResult(path);
                    break;
            }
        }
    }

    @OnClick(R.id.btn_feed_back)
    public void onClick() {
        commitCheck();
    }
}
