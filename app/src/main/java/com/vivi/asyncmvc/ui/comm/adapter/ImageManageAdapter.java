package com.vivi.asyncmvc.ui.comm.adapter;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.library.plugs.glide.AImage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 业务中的图片上传Adapter，如：一键挪车、意见反馈、文明报修
 *
 * @author gongwei
 * @date 2019.1.30
 */
public class ImageManageAdapter extends BaseAdapter {

    private int mMaxLength;//最多几张图片，用于控制是否显示“添加图片”item
    private Activity mActivity;
    private ArrayList<String> mSelectPath;//所选图片
    private ImageManageCallBack mCallBack;

    public ImageManageAdapter(Activity activity, int maxLength, ArrayList<String> selectPath, ImageManageCallBack callBack) {
        this.mActivity = activity;
        this.mMaxLength = maxLength;
        this.mSelectPath = selectPath;
        this.mCallBack = callBack;
        if (mSelectPath == null) {
            mSelectPath = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        if (mSelectPath == null || mSelectPath.size() == 0) {
            return 1;
        }
        if (mSelectPath.size() < mMaxLength) {
            return mSelectPath.size() + 1;//多一个添加按钮
        }
        return mSelectPath.size();
    }

    @Override
    public Object getItem(int position) {
        return mSelectPath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mSelectPath.size() < mMaxLength && position == mSelectPath.size()) {
            //显示添加按钮
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_image_manage_add, null);
            if (mCallBack != null) {
                convertView.findViewById(R.id.rlt_image_manage_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallBack.clickAdd();
                    }
                });
            }
        } else {
            //显示图片
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_image_manage, null);
            ImageView image = convertView.findViewById(R.id.iv_image_manage_content);
            ImageView imageDelete = convertView.findViewById(R.id.iv_image_manage_delete);
            AImage.load(mSelectPath.get(position), image);
            if (mCallBack != null) {
                image.setTag(R.id.imgIndexTag, position);
                imageDelete.setTag(R.id.imgIndexTag, position);
                View.OnTouchListener touchListener = new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            int position = (int) view.getTag(R.id.imgIndexTag);
                            switch (view.getId()) {
                                case R.id.iv_image_manage_content:
                                    mCallBack.clickImage(position);
                                    break;
                                case R.id.iv_image_manage_delete:
                                    mCallBack.clickDelete(position);
                                    break;
                            }
                        }
                        return true;
                    }
                };
                image.setOnTouchListener(touchListener);
                imageDelete.setOnTouchListener(touchListener);
            }
        }
        return convertView;
    }

    public interface ImageManageCallBack {
        void clickImage(int index);

        void clickDelete(int index);

        void clickAdd();
    }
}
