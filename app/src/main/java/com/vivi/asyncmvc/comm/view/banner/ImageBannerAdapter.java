package com.vivi.asyncmvc.comm.view.banner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.view.roundimg.RoundedImageView;
import com.vivi.asyncmvc.library.plugs.glide.AImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:图片Banner适配器
 *
 * @Author: gongwei
 * @Date: 2019/1/10
 */
public class ImageBannerAdapter extends LoopViewPager.BannerPagerAdapter {

    private List<String> mImageList = new ArrayList();;
    private ImageBannerCallBack mCallBack;
    private float mRoundCorner = 0; //圆角，0为直角

    /**
     * @param roundCorner 圆角，0为直角
     */
    public void setRoundCorner(float roundCorner) {
        this.mRoundCorner = roundCorner;
    }

    public List<String> getImageList() {
        return mImageList;
    }

    public void clear() {
        mImageList.clear();
    }

    /**
     * @param objects
     * objects[0]: List<String>
     * objects[1]: ImageBannerCallBack
     */
    @Override
    public void setData(Object... objects) {
        this.mImageList = (List<String>) objects[0];
        this.mCallBack = (ImageBannerCallBack) objects[1];
        if (this.mImageList == null) {
            this.mImageList = new ArrayList();
        }
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView;
        if (mRoundCorner != 0) {
            imageView = new RoundedImageView(container.getContext());
            ((RoundedImageView) imageView).setCornerRadius(mRoundCorner);
        } else {
            imageView = new ImageView(container.getContext());
        }

        imageView.setContentDescription(String.valueOf(position));
        imageView.setImageResource(R.drawable.ic_img_default);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(container.getMeasuredWidth(), container.getMeasuredHeight());
        imageView.setLayoutParams(params);
        AImage.loadCenterCrop(mImageList.get(position), imageView);
        if (mCallBack != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.clickItem(position);
                }
            });
        }
        container.addView(imageView);
        return imageView;
    }

    public interface ImageBannerCallBack {
        void clickItem(int position);
    }
}
