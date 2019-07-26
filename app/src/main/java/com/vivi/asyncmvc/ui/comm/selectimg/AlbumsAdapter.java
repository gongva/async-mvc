package com.vivi.asyncmvc.ui.comm.selectimg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.Folder;
import com.vivi.asyncmvc.library.plugs.glide.AImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongwei on 2019.1.3
 */
public class AlbumsAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    private List<Folder> mFolders = new ArrayList<>();

    int mImageSize;

    public AlbumsAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageSize = mContext.getResources().getDimensionPixelOffset(R.dimen.folder_cover_size);
    }

    /**
     * 设置数据集
     *
     * @param folders
     */
    public void setData(List<Folder> folders) {
        if (folders != null && folders.size() > 0) {
            mFolders = folders;
        } else {
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size() + 1;
    }

    @Override
    public Folder getItem(int position) {
        if (position == 0)
            return null;
        return mFolders.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_albums, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if (i == 0) {
                holder.name.setText("照片库");
                holder.size.setText(String.valueOf(getTotalImageSize()));
                if (mFolders.size() > 0) {
                    Folder f = mFolders.get(0);
                    AImage.loadCenterCropResizeFile(new File(f.cover.path), holder.cover, mImageSize, mImageSize);
                }
            } else {
                holder.bindData(getItem(i));
            }
        }
        return view;
    }

    private int getTotalImageSize() {
        int result = 0;
        if (mFolders != null && mFolders.size() > 0) {
            for (Folder f : mFolders) {
                result += f.images.size();
            }
        }
        return result;
    }

    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView size;

        ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.iv_albums_cover);
            name = (TextView) view.findViewById(R.id.tv_albums_name);
            size = (TextView) view.findViewById(R.id.tv_albums_size);
            view.setTag(this);
        }

        void bindData(Folder data) {
            name.setText(data.name);
            size.setText(String.valueOf(data.images.size()));
            AImage.loadCenterCropResizeFile(new File(data.cover.path), cover, mImageSize, mImageSize);
        }
    }
}
