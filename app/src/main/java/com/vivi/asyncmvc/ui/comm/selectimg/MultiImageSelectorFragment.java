package com.vivi.asyncmvc.ui.comm.selectimg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.studio.util.DateUtils;
import android.studio.view.ViewUtils;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.Folder;
import com.vivi.asyncmvc.api.entity.Image;
import com.vivi.asyncmvc.base.BaseFragment;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageAlbumChangeEvent;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.CommonTools;
import com.vivi.asyncmvc.library.utils.FileUtil;
import com.vivi.asyncmvc.library.utils.UI;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图片选择Fragment
 *
 * @author gongwei 2019.1.3
 */
public class MultiImageSelectorFragment extends BaseFragment {

    /**
     * 最大图片选择次数，int类型
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，int类型
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，boolean类型
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 是否用于快速选图
     */
    public static final String EXTRA_FOR_FAST_CHOOSE = "extra_for_fast_choose";
    /**
     * 默认选择的数据集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";

    // 结果数据
    private ArrayList<String> resultList = new ArrayList<>();
    // 文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    // 图片Grid
    private GridView mGridView;
    private ImageLoaderCallbacks.ImageSelectedHandler mCallback;

    private ImageGridAdapter mImageAdapter;

    // 时间线
    private TextView mTimeLineText;

    private int mDesireImageCount;

    private File mTmpFile;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (ImageLoaderCallbacks.ImageSelectedHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_multi_image_selector;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BusProvider.register(this);
        hideActionBar();

        // 选择图片数量
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

        // 图片选择模式
        final int mode = getArguments().getInt(EXTRA_SELECT_MODE);

        // 默认选择
        if (mode == ImageLoaderCallbacks.MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                resultList = tmp;
            }
        }

        // 是否显示照相机
        final boolean showCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, false);

        // 是否用于快速选图
        final boolean isForFastChoose = getArguments().getBoolean(EXTRA_FOR_FAST_CHOOSE, false);

        mImageAdapter = new ImageGridAdapter(getActivity(), showCamera, mDesireImageCount, new ImageGridAdapter.CallBackMultiImageSelector() {
            @Override
            public void clickImage(int position) {
                if (isForFastChoose) {
                    mCallback.onSingleImageSelected(mImageAdapter.getData().get(position).path);
                } else {
                    List<Image> allPics = mImageAdapter.getData();
                    ArrayList<String> picList = new ArrayList();
                    for (Image image : allPics) {
                        picList.add(image.path);
                    }
                    BrowseImgsActivity.startForSelect(getActivity(), picList, position, resultList, mDesireImageCount);
                }
            }

            @Override
            public void clickCheckBox(int position) {
                if (mImageAdapter.isShowCamera() && position == 0) {
                    // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                    if (mDesireImageCount == resultList.size()) {
                        UI.showMultiSelectLimitDialog(getActivity(), mDesireImageCount);
                    } else {
                        showCameraAction();
                    }
                } else {
                    // 正常操作
                    Image image = mImageAdapter.getItem(position);
                    selectImageFromGrid(image, mode);
                }
            }
        });
        // 是否显示选择指示器，快速选图不需要指示器
        mImageAdapter.showSelectIndicator(!isForFastChoose);

        // 如果显示了照相机，则创建临时文件
        if (showCamera) {
            mTmpFile = FileUtil.createImageTmpFile(getActivity());
        }

        mTimeLineText = view.findViewById(R.id.tv_multi_image_selector_timeline_area);
        // 初始化，先隐藏当前timeline
        mTimeLineText.setVisibility(View.GONE);

        mGridView = view.findViewById(R.id.gd_multi_image_selector);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {
                if (state == SCROLL_STATE_IDLE) {
                    // 停止滑动，日期指示器消失
                    mTimeLineText.setVisibility(View.GONE);
                } else if (state == SCROLL_STATE_FLING) {
                    mTimeLineText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mTimeLineText.getVisibility() == View.VISIBLE) {
                    int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1
                            : firstVisibleItem + 1;
                    Image image = (Image) view.getAdapter().getItem(index);
                    if (image != null) {
                        File file = new File(image.path);
                        if (file.exists()) {
                            long time = file.lastModified();
                            mTimeLineText.setText(DateUtils.formatDate(new Date(time)));
                        }
                    }
                }
            }
        });
        mGridView.setAdapter(mImageAdapter);
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {
                final int numCount = mGridView.getNumColumns();
                final int columnSpace = ViewUtils.dip2px(getActivity(), 2);
                int columnWidth = (mGridView.getWidth() - columnSpace * (numCount - 1)) / numCount;
                mImageAdapter.setItemSize(columnWidth);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 首次加载所有图片
        getActivity().getSupportLoaderManager().initLoader(ImageLoaderCallbacks.LOADER_ALL, null, getLoaderCallback());
    }

    @Subscribe
    public void onEvent(SelectImageAlbumChangeEvent event) {
        if (event != null) {
            onAlbumChanged(event.albumPath);
        }
    }

    /*
     * 切换相册
     * */
    private void onAlbumChanged(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            getActivity().getSupportLoaderManager().restartLoader(ImageLoaderCallbacks.LOADER_ALL, null, getLoaderCallback());
        } else {
            Bundle args = new Bundle();
            args.putString("path", folderPath);
            getActivity().getSupportLoaderManager().restartLoader(ImageLoaderCallbacks.LOADER_CATEGORY, args, getLoaderCallback());
        }
        mGridView.smoothScrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
    }

    public void setDefaultSelected(ArrayList<String> l) {
        resultList.clear();
        resultList.addAll(l);
        mImageAdapter.setDefaultSelected(resultList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppContext.REQUEST_CODE_OPEN_CAMERA) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpFile);
                    }
                }
            }
        }
    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, CommonTools.getUriFromFileCompat(getActivity(), mTmpFile));
            startActivityForResult(cameraIntent, AppContext.REQUEST_CODE_OPEN_CAMERA);
        } else {
            Toast.makeText(getActivity(), "没有系统相机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选择图片操作
     *
     * @param image
     */
    private void selectImageFromGrid(Image image, int mode) {
        if (image != null) {
            // 多选模式
            if (mode == ImageLoaderCallbacks.MODE_MULTI) {
                if (resultList.contains(image.path)) {
                    resultList.remove(image.path);
                    if (mCallback != null) {
                        mCallback.onImageUnselected(image.path);
                    }
                } else {
                    // 判断选择数量问题
                    if (mDesireImageCount == resultList.size()) {
                        if (mDesireImageCount > 1) {
                            UI.showMultiSelectLimitDialog(getActivity(), mDesireImageCount);
                            return;
                        } else {
                            //add by gw. ：若上限为1张，选择后一张时把前面选的取消掉，再选中当前图片
                            resultList.clear();
                            mImageAdapter.mSelectedImages.clear();
                        }
                    }

                    resultList.add(image.path);
                    if (mCallback != null) {
                        mCallback.onImageSelected(image.path);
                    }
                }
                mImageAdapter.select(image);
            } else if (mode == ImageLoaderCallbacks.MODE_SINGLE) {
                // 单选模式
                if (mCallback != null) {
                    mCallback.onSingleImageSelected(image.path);
                }
            }
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> getLoaderCallback() {
        return new ImageLoaderCallbacks(getActivity(), new ImageLoaderCallbacks.ImageCallbacksHandler() {
            @Override
            public void handlerResult(ArrayList<Folder> folders, List<Image> images) {
                mImageAdapter.setData(images);

                // 设定默认选择
                if (resultList != null && resultList.size() > 0) {
                    mImageAdapter.setDefaultSelected(resultList);
                }
            }
        });
    }
}