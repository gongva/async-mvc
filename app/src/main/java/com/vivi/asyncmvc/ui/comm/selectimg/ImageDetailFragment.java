package com.vivi.asyncmvc.ui.comm.selectimg;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseFragment;
import com.vivi.asyncmvc.library.plugs.glide.AImage;
import com.vivi.asyncmvc.comm.view.photoview.PhotoViewAttacher;

/**
 * 大图浏览Fragment
 * Created by gw on 2019.1.3
 */
public class ImageDetailFragment extends BaseFragment {
    //data
    private String mImageUrl;
    private int mDefaultImg;
    //view
    protected ImageView mImageView;
    protected ProgressBar progressBar;
    //controller
    private PhotoViewAttacher mAttacher;
    private CallBackImageDetailFragment callBack;

    public static ImageDetailFragment newInstance(String imageUrl, int defaultImgResId) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        args.putInt("defaultImgResId", defaultImgResId);
        if (fragment != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    /**
     * 图片操作回调，使用时请设置
     *
     * @param callBack
     */
    public void setCallBack(CallBackImageDetailFragment callBack) {
        this.callBack = callBack;
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_image_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = (getArguments() != null ? getArguments().getString("url") : null);
        mDefaultImg = (getArguments() != null ? getArguments().getInt("defaultImgResId") : 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mImageView = rootView.findViewById(R.id.imageView);
        progressBar = rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mImageView != null) {
            mImageView.setImageDrawable(null);
        }
        if (mAttacher != null) {
            mAttacher.cleanup();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideActionBar();
        if (mImageView == null || progressBar == null) return;
        AImage.load(mImageUrl, mImageView, new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mImageView.setImageResource(mDefaultImg);
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callBack != null) {
                            callBack.imageClick();
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                mAttacher = new PhotoViewAttacher(mImageView);
                mAttacher.update();
                mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

                    @Override
                    public void onViewTap(View view, float x, float y) {
                        if (callBack != null) {
                            callBack.imageClick();
                        }
                    }
                });
                mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (callBack != null) {
                            callBack.imageLongClick();
                        }
                        return false;
                    }
                });
                return false;
            }
        });
    }

    public Bitmap getDrawingCache() {
        if (mImageView != null) {
            return mImageView.getDrawingCache();
        }
        return null;
    }

    public interface CallBackImageDetailFragment {
        void imageClick();

        void imageLongClick();
    }
}
