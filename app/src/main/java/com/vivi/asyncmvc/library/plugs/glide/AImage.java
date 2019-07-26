package com.vivi.asyncmvc.library.plugs.glide;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.library.utils.CommonTools;

import java.io.File;

/**
 * 图片加载工具类
 */
public class AImage {

    private static int defaultResId = R.drawable.ic_img_default;

    private static RequestOptions getOptions() {
        return new RequestOptions().placeholder(defaultResId);
    }

    //For resource
    public static void load(int res, ImageView imageView) {
        imageView.setImageResource(res);
    }


    //For url
    public static void load(String url, ImageView imageView) {
        load(url, null, imageView);
    }

    public static void load(String url, ImageView imageView, RequestListener listener) {
        load(url, getOptions(), imageView, listener);
    }

    public static void loadCenterCrop(String url, ImageView imageView) {
        load(url, getOptions().centerCrop(), imageView);
    }

    public static void load(String url, RequestOptions options, ImageView imageView) {
        load(url, options, imageView, null);
    }

    public static void load(String url, RequestOptions options, ImageView imageView, RequestListener listener) {
        if (options == null) options = new RequestOptions().placeholder(defaultResId);
        if (CommonTools.isServerImageUrl(url)) {
            url = AppContext.BASE_URL_IMAGE + url;
        }
        Glide.with(imageView.getContext()).load(url).apply(options).listener(listener).into(imageView);
    }


    // For file
    public static void loadCenterCropResizeFile(File file, ImageView imageView, int width, int height) {
        load(file, getOptions().override(width, height).centerCrop(), imageView, null);
    }

    public static void load(File file, RequestOptions options, ImageView imageView, RequestListener listener) {
        if (options == null) options = new RequestOptions().placeholder(defaultResId);
        Glide.with(imageView.getContext()).load(file).apply(options).listener(listener).into(imageView);
    }
}
