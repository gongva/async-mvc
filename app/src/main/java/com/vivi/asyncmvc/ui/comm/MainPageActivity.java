package com.vivi.asyncmvc.ui.comm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import butterknife.BindView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.UrlEntity;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.event.LoginErrorEvent;
import com.vivi.asyncmvc.comm.event.LogoffEvent;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.managers.upgrade.DownloadChangeObserver;
import com.vivi.asyncmvc.comm.managers.upgrade.UpgradeManager;
import com.vivi.asyncmvc.ui.comm.selectimg.MultiImageSelectorActivity;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.CommonTools;
import com.vivi.asyncmvc.library.utils.FileUtil;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.car.CarFragment;
import com.vivi.asyncmvc.ui.home.homepage.HomeFragment;
import com.vivi.asyncmvc.ui.login.LoginActivity;
import com.vivi.asyncmvc.ui.me.MeFragment;
import com.vivi.asyncmvc.ui.comm.adapter.MainFragmentPagerAdapter;
import com.vivi.asyncmvc.comm.view.PointRadioButton;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainPageActivity extends BaseActivity {

    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;
    @BindView(R.id.main_tabToolbar)
    RadioGroup tabToolbar;
    @BindView(R.id.radio_main_home)
    PointRadioButton radioHome;
    @BindView(R.id.radio_main_car)
    PointRadioButton radioCar;
    @BindView(R.id.radio_main_me)
    PointRadioButton radioMe;

    //for双击退出
    private long mExitTime;
    //Fragment Adapter
    private MainFragmentPagerAdapter mAdapter;
    //当前Tab index
    private int mCurrentTabIndex = 0;
    //监听apk下载更新进度的观察者
    private DownloadChangeObserver observer;
    //Fragment
    private MeFragment meFragment;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main_page;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainPageActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setStatusBarTransparent(true);
        super.setStatusIconDark(false);
        BusProvider.bindLifecycle(this);
        setActionBarEnabled(false);
        initViews();

        //request permissions
        super.requestRunPermission(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,}, new PermissionRequestListener() {
            @Override
            public void onGranted() {
            }
        });
        //检查更新
        UpgradeManager.getInstance().checkAppUpgradeStart(null);
    }

    private void initViews() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new CarFragment());
        fragments.add(meFragment = new MeFragment());
        mAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mainViewPager.setAdapter(mAdapter);
        mainViewPager.setOffscreenPageLimit(fragments.size() - 1);
        mainViewPager.setOnPageChangeListener(getPageChangeListener());
        tabToolbar.check(R.id.radio_main_home);
        tabToolbar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = tabToolbar.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = tabToolbar.getChildAt(i);
                    if (child.getId() == checkedId) {
                        mCurrentTabIndex = i;
                        break;
                    }
                }
                mainViewPager.setCurrentItem(mCurrentTabIndex);
            }
        });
    }

    private ViewPager.OnPageChangeListener getPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position != mCurrentTabIndex) {
                    mCurrentTabIndex = position;
                    View child = tabToolbar.getChildAt(position);
                    tabToolbar.check(child.getId());
                }
                setStatusIconDark(position == 1);//爱车的状态栏图标为黑色，其他tab为白色
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
    }

    /**
     * 裁剪头像
     *
     * @param imagePath
     */
    private void openHeadCrop(String imagePath) {
        CommonTools.openCrop(this, imagePath, 3, 3, 300, 300);
    }

    /**
     * 上传头像
     * 用于MeFragment的头像上传
     *
     * @param path
     */
    private void uploadHead(final String path) {
        // 设置头像
        showLoadingDialog("正在上传头像");
        final String pathScaled = FileUtil.saveImageToSDCard(FileUtil.getImage(path));
        File file = new File(pathScaled);
        Api.uploadImage(file, new JsonResultCallback<JsonResult<UrlEntity>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<UrlEntity> response, int tag) {
                UrlEntity urlEntity = response.getData();
                if (urlEntity != null && !TextUtils.isEmpty(urlEntity.url)) {
                    updateHead(urlEntity.url);
                } else {
                    dismissLoadingDialog();
                    UI.showToast("噢呃，上传出错了");
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                dismissLoadingDialog();
                UI.showToast(responseString);
            }
        });
    }

    /**
     * 更新头像
     *
     * @param avatar
     */
    private void updateHead(final String avatar) {
        Api.updateHead(avatar, new JsonResultCallback<JsonResultVoid>() {
            @Override
            public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                dismissLoadingDialog();
                UI.showToast("头像修改成功");
                if (meFragment != null) {
                    meFragment.setAvatar(avatar);
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                dismissLoadingDialog();
                UI.showToast(responseString);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppContext.REQUEST_CODE_SELECTED_IMAGES:
                    //选相册回来：头像
                    ArrayList<String> temp = MultiImageSelectorActivity.getImagesFromResultOk(data);
                    if (temp != null && temp.size() > 0) {
                        openHeadCrop(temp.get(0));
                    }
                    break;
                case AppContext.REQUEST_CODE_OPEN_CAMERA:
                    //拍照回来：头像
                    String path = CommonTools.getCameraFilePath();
                    if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                        UI.showToast("未找到照片");
                        return;
                    }
                    openHeadCrop(path);
                    break;
                case AppContext.REQUEST_CODE_IMAGE_CROP:
                    File f = CommonTools.getCropTmpFile(this);
                    if (f.exists()) {
                        try {
                            String pathTemp = CommonTools.newHikPicPath();
                            File file = new File(pathTemp);
                            file.createNewFile();
                            CommonTools.copyFile(f, file);
                            String headLocal = CommonTools.getCameraFilePath();
                            uploadHead(headLocal);
                        } catch (IOException e) {
                            e.printStackTrace();
                            UI.showToast("图片裁剪失败");
                        }
                    } else {
                        UI.showToast("图片裁剪失败");
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            UI.showToast("再按一次退出程序");
            mExitTime = System.currentTimeMillis();
        } else {
            exitApp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
        }
    }

    @Subscribe
    public void onEvent(LogoffEvent event) {
        LoginManager.getInstance().exitAccount();
        exitApp();
        LoginActivity.startForLogoff(this, event.msg);
    }

    @Subscribe
    public void onEvent(LoginErrorEvent event) {
        UI.showToast(event.msg);
        LoginManager.getInstance().exitAccount();
        exitApp();
        LoginActivity.start(this);
    }
}