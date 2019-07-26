package com.vivi.asyncmvc.ui.comm.selectimg;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.ActivitySupport;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageCancelEvent;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageCompleteEvent;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.UI;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongwei on 2019.1.3
 */
public class AlbumsActivity extends BaseActivity {

    /**
     * 常规选择相册目录时使用
     * @param context
     * @param selectImages
     * @param selectCount
     */
    public static void start(Activity context, ArrayList<String> selectImages, int selectCount) {
        Intent intent = new Intent(context, AlbumsActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, selectImages);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, selectCount);
        start(context, intent);
    }

    /**
     * 快速选择图片时使用，点击图片列表即返回
     * @param context
     * @param selectImages
     * @param selectCount
     */
    public static void startForFastChoose(Activity context, ArrayList<String> selectImages, int selectCount) {
        Intent intent = new Intent(context, AlbumsActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, selectImages);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, selectCount);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.translate_in_from_left, R.anim.browse_img_open_for_out);
                    }
                }, 200);
            }
        });
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_albums;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.register(this);
        hideActionBar();

        super.requestRunPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionRequestListener() {
            @Override
            public void onGranted() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onCreateNext();
                    }
                }, 200);
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                UI.showConfirmDialog(AlbumsActivity.this, "权限申请未通过，无法使用相册", "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageClose();
                    }
                });
            }
        });
    }

    private void onCreateNext() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, getIntent().getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST));
        bundle.putInt(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, getIntent().getIntExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9));
        bundle.putBoolean(MultiImageSelectorActivity.EXTRA_FOR_FAST_CHOOSE, getIntent().getBooleanExtra(MultiImageSelectorActivity.EXTRA_FOR_FAST_CHOOSE, false));

        AlbumsFragment fragment = (AlbumsFragment) Fragment.instantiate(this, AlbumsFragment.class.getName(), bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fly_albums, fragment)
                .commit();
    }

    /*
    * 取消选图，关闭本页面，带动画
    * */
    public void pageClose() {
        finish();
        overridePendingTransition(R.anim.browse_img_close_for_in, R.anim.translate_out_from_bottom);
    }

    /*
    * 进入某个相册，关闭本页面，带动画
    * */
    public void pageToMulti() {
        finish();
        overridePendingTransition(R.anim.browse_img_close_for_in, R.anim.translate_out_from_left);
    }

    @Subscribe
    public void onEvent(SelectImageCompleteEvent event) {
        pageClose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
    }

    @Override
    public void onBackPressed() {
        BusProvider.post(new SelectImageCancelEvent());
        pageClose();
    }
}
