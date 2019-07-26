package com.vivi.asyncmvc.ui.comm.selectimg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.Folder;
import com.vivi.asyncmvc.api.entity.Image;
import com.vivi.asyncmvc.base.BaseFragment;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageAlbumChangeEvent;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageCancelEvent;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageCompleteEvent;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageDoingEvent;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择照片：相册列表
 * Created by gongwei on 2019.1.3
 */
public class AlbumsFragment extends BaseFragment {
    @BindView(R.id.v_albums_bottom)
    View vAlbumsBottom;
    @BindView(R.id.btn_albums_preview)
    TextView btnAlbumsPreview;
    @BindView(R.id.btn_albums_commit)
    TextView btnAlbumsCommit;
    @BindView(R.id.tv_albums_selected_count)
    TextView tvAlbumsSelectedCount;
    @BindView(R.id.lv_albums)
    ListView lvAlbums;

    private ArrayList<String> mSelectImages;//已选照片列表

    private int mSelectCount;//可选照片最大数量

    private AlbumsAdapter mAlbumsAdapter;

    private View mRootView;

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_albums;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, mRootView);
        BusProvider.bindLifecycle(this);
        hideActionBar();

        mSelectImages = getArguments().getStringArrayList(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST);
        mSelectCount = getArguments().getInt(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        boolean isForFastChoose = getArguments().getBoolean(MultiImageSelectorActivity.EXTRA_FOR_FAST_CHOOSE, false);//是否是用于快速选图

        // init data
        if (isForFastChoose) {
            vAlbumsBottom.setVisibility(View.GONE);
        } else {
            refreshBottomView();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAlbumsAdapter = new AlbumsAdapter(getActivity());
        lvAlbums.setAdapter(mAlbumsAdapter);

        // init listener
        lvAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectImageAlbumChangeEvent event;
                if (position == 0) {
                    event = new SelectImageAlbumChangeEvent("照片库", null);
                } else {
                    event = new SelectImageAlbumChangeEvent(mAlbumsAdapter.getItem(position).name, mAlbumsAdapter.getItem(position).path);
                }
                BusProvider.post(event);
                ((AlbumsActivity) getActivity()).pageToMulti();
            }
        });
        getActivity().getSupportLoaderManager().initLoader(ImageLoaderCallbacks.LOADER_ALL, null, new ImageLoaderCallbacks(getActivity(), new ImageLoaderCallbacks.ImageCallbacksHandler() {
            @Override
            public void handlerResult(ArrayList<Folder> folders, List<Image> images) {
                mAlbumsAdapter.setData(folders);
            }
        }));
    }

    @Subscribe
    public void onEvent(SelectImageDoingEvent event) {
        mSelectImages = event.r;
        refreshBottomView();
    }

    @OnClick({R.id.btn_albums_back, R.id.btn_albums_preview, R.id.btn_albums_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_albums_back:
                BusProvider.post(new SelectImageCancelEvent());
                ((AlbumsActivity) getActivity()).pageClose();
                break;
            case R.id.btn_albums_preview:
                //预览
                BrowseImgsActivity.startForSelect(getActivity(), mSelectImages, 0, mSelectImages, mSelectCount);
                break;
            case R.id.btn_albums_commit:
                BusProvider.post(new SelectImageCompleteEvent(mSelectImages));
                ((AlbumsActivity) getActivity()).pageClose();
                break;
        }
    }

    /* 刷新底部已选数量 */
    private void refreshBottomView() {
        if (mSelectImages != null && mSelectImages.size() > 0) {
            if (mSelectCount == 1) {
                tvAlbumsSelectedCount.setVisibility(View.GONE);
            } else {
                tvAlbumsSelectedCount.setVisibility(View.VISIBLE);
                tvAlbumsSelectedCount.setText(String.valueOf(mSelectImages.size()));
            }
            btnAlbumsCommit.setEnabled(true);
            btnAlbumsPreview.setEnabled(true);
            btnAlbumsCommit.setTextColor(getActivity().getResources().getColor(R.color.white));
            btnAlbumsPreview.setTextColor(getActivity().getResources().getColor(R.color.white));
        } else {
            tvAlbumsSelectedCount.setVisibility(View.GONE);
            btnAlbumsCommit.setEnabled(false);
            btnAlbumsPreview.setEnabled(false);
            btnAlbumsCommit.setTextColor(getActivity().getResources().getColor(R.color.btn_disable));
            btnAlbumsPreview.setTextColor(getActivity().getResources().getColor(R.color.btn_disable));
        }
    }
}
