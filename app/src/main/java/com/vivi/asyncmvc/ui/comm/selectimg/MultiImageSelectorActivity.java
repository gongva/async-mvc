package com.vivi.asyncmvc.ui.comm.selectimg;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.ActivitySupport;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageAlbumChangeEvent;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageCancelEvent;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageCompleteEvent;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageDoingEvent;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;

/**
 * 多图选择
 * @author gongwei on 2019.1.3
 */
public class MultiImageSelectorActivity extends BaseActivity implements ImageLoaderCallbacks.ImageSelectedHandler, View.OnClickListener {

    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 选择结果，是否为医生课件发送到白板
     */
    public static final String RESULT_SEND_WHITEBOARD = "extra_send_whiteboard";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    /**
     * 是否用于快速选图
     */
    public static final String EXTRA_FOR_FAST_CHOOSE = "extra_for_fast_choose";
    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;

    private ArrayList<String> resultList = new ArrayList<>();
    private TextView mTvTitle;
    private TextView mTvSelectedNum;
    private TextView mPreviewButton;
    private TextView mSubmitButton;
    private int mDefaultCount;
    private boolean isForFastChoose;//是否用于快速选图
    private MultiImageSelectorFragment mFragment;
    private int mode;
    private boolean isShow;

    /**
     * 选多张图
     * @param context
     * @param selectCount 数量上限
     * @param mSelectPath 已选的图片列表
     */
    public static void startForMulti(Activity context, int selectCount, ArrayList<String> mSelectPath) {
        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, selectCount);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        start(context, intent);
    }

    /**
     * 选单张图
     * @param context
     */
    public static void startForSingle(Activity context) {
        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        start(context, intent);
    }

    /**
     * 快速选择，点击列表及返回
     * 可用于头像选择
     * @param context
     */
    public static void startForFastChoose(Activity context) {
        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_FOR_FAST_CHOOSE, true);
        start(context, intent);
    }

    /**
     * 请求权限并跳转
     * @param context
     * @param intent
     */
    private static void start(final Activity context, final Intent intent) {
        ActivitySupport.requestRunPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionRequestListener() {
            @Override
            public void onGranted() {
                context.startActivityForResult(intent, AppContext.REQUEST_CODE_SELECTED_IMAGES);
                context.overridePendingTransition(R.anim.translate_in_from_bottom, R.anim.browse_img_open_for_out);
            }
        });
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_multi_image_selector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.bindLifecycle(this);
        hideActionBar();

        // 返回按钮
        findViewById(R.id.tv_multi_image_back).setOnClickListener(this);
        //  取消按钮按钮
        findViewById(R.id.tv_multi_image_cancel).setOnClickListener(this);
        //标题
        mTvTitle = findViewById(R.id.tv_multi_image_title);
        //已选
        mTvSelectedNum = findViewById(R.id.tv_multi_image_select_data);
        mPreviewButton = findViewById(R.id.btn_multi_image_selects_preview);
        // 完成按钮
        mSubmitButton = findViewById(R.id.btn_multi_image_selects_commit);

        mPreviewButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        isForFastChoose = intent.getBooleanExtra(EXTRA_FOR_FAST_CHOOSE, false);//是否是用于快速选图
        mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        //结果集
        if (resultList == null) {
            resultList = new ArrayList();
        }
        onCreateNext();
    }

    private void onCreateNext() {
        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_FOR_FAST_CHOOSE, isForFastChoose);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);
        mFragment = (MultiImageSelectorFragment) Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, mFragment)
                .commit();

        if (isForFastChoose) {
            findViewById(R.id.v_multi_image_bottom).setVisibility(View.GONE);
        } else {
            refreshSelectedNum();
        }

    }

    @Subscribe
    public void onEvent(SelectImageCompleteEvent event) {
        resultOk(event == null ? null : event.r);
    }

    @Subscribe
    public void onEvent(SelectImageCancelEvent event) {
        closePage();
    }

    @Subscribe
    public void onEvent(SelectImageAlbumChangeEvent event) {
        if (event != null) {
            mTvTitle.setText(event.albumName);
        }
    }

    @Subscribe
    public void onEvent(SelectImageDoingEvent event) {
        resultList.clear();
        resultList.addAll(event.r);
        mFragment.setDefaultSelected(event.r);
        refreshSelectedNum();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void clickBack() {
        if (isForFastChoose) {
            AlbumsActivity.startForFastChoose(this, resultList, mDefaultCount);
        } else {
            AlbumsActivity.start(this, resultList, mDefaultCount);
        }
    }

    /**
     * 更新已选照片数量
     */
    private void refreshSelectedNum() {
        if (!isForFastChoose) {
            if (resultList.size() > 0) {
                if (mDefaultCount == 1) {
                    mTvSelectedNum.setVisibility(View.GONE);
                } else {
                    mTvSelectedNum.setVisibility(View.VISIBLE);
                    mTvSelectedNum.setText(String.valueOf(resultList.size()));
                }
                mSubmitButton.setEnabled(true);
                mPreviewButton.setEnabled(true);
                mSubmitButton.setTextColor(getResources().getColor(R.color.white));
                mPreviewButton.setTextColor(getResources().getColor(R.color.white));
            } else {
                mTvSelectedNum.setVisibility(View.GONE);
                mSubmitButton.setEnabled(false);
                mPreviewButton.setEnabled(false);
                mSubmitButton.setTextColor(getResources().getColor(R.color.btn_disable));
                mPreviewButton.setTextColor(getResources().getColor(R.color.btn_disable));
            }
        }
    }

    /**
     * ResultOk返回数据
     */
    private void resultOk(ArrayList<String> r) {
        Intent data = new Intent();
        data.putStringArrayListExtra(EXTRA_RESULT, r);
        setResult(RESULT_OK, data);
        closePage();
    }

    /**
     * 封装一个从ResultOK的Intent中获取编辑后的图片列表的方法
     * startActivityForResult过来的Activity使用
     * @param data
     * @return
     */
    public static ArrayList getImagesFromResultOk(Intent data) {
        if (data != null) {
            return data.getStringArrayListExtra(EXTRA_RESULT);
        }
        return null;
    }

    private void closePage() {
        finish();
        overridePendingTransition(R.anim.browse_img_close_for_in, R.anim.translate_out_from_bottom);
    }

    @Override
    public void onSingleImageSelected(String path) {
        resultList.add(path);
        resultOk(resultList);
    }

    @Override
    public void onImageSelected(String path) {
        //add by gw.：若上限为1张，选择后一张时把前面选的取消掉，再选中当前图片
        if (mDefaultCount == 1) {
            resultList.clear();
        }

        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        refreshSelectedNum();
    }

    @Override
    public void onImageUnselected(String path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
        }
        refreshSelectedNum();
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            resultList.add(imageFile.getAbsolutePath());
            resultOk(resultList);
        }
    }

    @Override
    public void addAllList(ArrayList<String> list) {

    }

    @Override
    public void onBackPressed() {
        clickBack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_multi_image_back:
                //返回（相册）
                clickBack();
                break;
            case R.id.tv_multi_image_cancel:
                //取消
                closePage();
                break;
            case R.id.btn_multi_image_selects_commit:
                //完成
                if (resultList != null && resultList.size() > 0) {
                    // 返回已选择的图片数据
                    if (resultList != null && resultList.size() > 0) {
                        resultOk(resultList);
                    }
                    closePage();
                }
                break;
            case R.id.btn_multi_image_selects_preview:
                //预览
                BrowseImgsActivity.startForSelect(this, resultList, 0, resultList, mDefaultCount);
                break;
        }
    }
}
