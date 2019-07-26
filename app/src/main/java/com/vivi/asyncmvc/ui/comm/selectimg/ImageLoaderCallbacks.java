package com.vivi.asyncmvc.ui.comm.selectimg;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.vivi.asyncmvc.api.entity.Folder;
import com.vivi.asyncmvc.api.entity.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 从本地获取图片
 * <p/>
 * Created by gongwei on 2019.1.3
 */
public class ImageLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    //单选
    public static final int MODE_SINGLE = 0;
    //多选
    public static final int MODE_MULTI = 1;

    // 不同loader定义
    public static final int LOADER_ALL = 0;
    public static final int LOADER_CATEGORY = 1;

    private Context context;
    private ImageCallbacksHandler handler;

    private final String[] IMAGE_PROJECTION = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media._ID};

    public ImageLoaderCallbacks(Context context, ImageCallbacksHandler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ALL) {
            CursorLoader cursorLoader = new CursorLoader(context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[2]
                    + " DESC");
            return cursorLoader;
        } else if (id == ImageLoaderCallbacks.LOADER_CATEGORY) {
            CursorLoader cursorLoader = new CursorLoader(context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[0]
                    + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            List<Image> images = new ArrayList<>();
            ArrayList<Folder> folders = new ArrayList<>();// 相册数据
            int count = data.getCount();
            if (count > 0) {
                data.moveToFirst();
                do {
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    Image image = new Image(path, name, dateTime);
                    images.add(image);
                    // 获取文件夹名称
                    File imageFile = new File(path);
                    File folderFile = imageFile.getParentFile();
                    Folder folder = new Folder();
                    folder.name = folderFile.getName();
                    folder.path = folderFile.getAbsolutePath();
                    folder.cover = image;
                    if (!folders.contains(folder)) {
                        List<Image> imageList = new ArrayList<>();
                        imageList.add(image);
                        folder.images = imageList;
                        folders.add(folder);
                    } else {
                        // 更新
                        Folder f = folders.get(folders.indexOf(folder));
                        f.images.add(image);
                    }

                } while (data.moveToNext());
                if (handler != null) {
                    handler.handlerResult(folders, images);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface ImageCallbacksHandler {
        void handlerResult(ArrayList<Folder> folders, List<Image> images);
    }

    public interface ImageSelectedHandler {
        void onSingleImageSelected(String path);

        void onImageSelected(String path);

        void onImageUnselected(String path);

        void onCameraShot(File imageFile);

        void addAllList(ArrayList<String> list);
    }
}
