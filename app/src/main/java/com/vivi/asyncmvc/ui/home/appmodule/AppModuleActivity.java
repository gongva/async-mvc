package com.vivi.asyncmvc.ui.home.appmodule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.AppModule;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.library.utils.UI;

import java.util.List;

import butterknife.BindView;

/**
 * 业务大厅
 *
 * @author gongwei
 * @Date 2019/1/14
 */
public class AppModuleActivity extends BaseActivity implements AppModuleView.AppModuleCallBack {

    //tools
    private final int MY_MODULE_MAX_COUNT = 7;//我的应用最多7个
    private final int MY_MODULE_MIN_COUNT = 3;//我的应用最少3个
    private boolean isEditing = false;//是否正在编辑模式下
    //data
    private List<AppModule> mMyModules;
    //views
    @BindView(R.id.amv_mine)
    AppModuleAreaMineView amvMine;
    @BindView(R.id.amv_driver_license)
    AppModuleAreaView amvDriverLicense;
    @BindView(R.id.amv_car_license)
    AppModuleAreaView amvCarLicense;
    @BindView(R.id.amv_nearby)
    AppModuleAreaView amvNearby;

    public static void start(Context context) {
        context.startActivity(new Intent(context, AppModuleActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_app_module;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("业务大厅");
        initView();
    }

    private void initView() {
        //若正在编辑，退出时确认
        setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backConfirm();
            }
        });
        //初始化数据
        amvDriverLicense.setModules("驾驶证", AppModule.getDriverLicenseModules(), this);
        amvCarLicense.setModules("机动车", AppModule.getCarLicenseModules(), this);
        amvNearby.setModules("出行周边", AppModule.getNearbyModules(), this);

        amvMine.setEditClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditModel();
            }
        });
    }

    /**
     * 打开编辑模式
     */
    private void openEditModel() {
        isEditing = true;
        setMenu("保存", 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEditModel();
                hideMenu();
            }
        });
        mMyModules = AppModule.getMyModules();
        refreshModules();
    }

    private void refreshModules() {
        amvMine.openEditModel(mMyModules, this);
        amvDriverLicense.openEditModel();
        amvCarLicense.openEditModel();
        amvNearby.openEditModel();
    }

    /**
     * 关闭编辑模式
     */
    private void closeEditModel() {
        isEditing = false;
        AppModule.saveMyModules(mMyModules);
        amvMine.closeEditModel();
        amvDriverLicense.closeEditModel();
        amvCarLicense.closeEditModel();
        amvNearby.closeEditModel();
    }

    @Override
    public void clickModule(AppModule appModule) {
        AppModule.startToModuleActivity(this, appModule);
    }

    @Override
    public boolean clickController(AppModuleView view, final AppModule appModule, boolean actionAdd) {
        if (actionAdd) {
            if (mMyModules.size() >= MY_MODULE_MAX_COUNT) {
                UI.showConfirmDialog(this, String.format("最多只允许添加%s个应用哦", MY_MODULE_MAX_COUNT), "知道了", null);
                return false;
            }
            addAnimation(view, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mMyModules.add(appModule);
                    refreshModules();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return true;
        } else {
            if (mMyModules.size() <= MY_MODULE_MIN_COUNT) {
                UI.showConfirmDialog(this, String.format("至少需要保留%s个应用哦", MY_MODULE_MIN_COUNT), "知道了", null);
                return false;
            }
            AppModule.removeModule(mMyModules, appModule);
            refreshModules();
            return true;
        }
    }

    /**
     * 添加时增加一个动画，页面增加一些反馈不至于突兀
     * @param view
     * @param animationListener
     */
    private void addAnimation(AppModuleView view, Animation.AnimationListener animationListener) {
        AlphaAnimation alphaAni = new AlphaAnimation(1, 0.2f);
        alphaAni.setDuration(300);
        alphaAni.setRepeatMode(Animation.REVERSE);
        alphaAni.setAnimationListener(animationListener);
        view.startAnimation(alphaAni);
    }

    /**
     * 编辑模式下退出前确认
     */
    private void backConfirm() {
        if (isEditing) {
            UI.showConfirmDialog(AppModuleActivity.this, "温馨提示", "您正在编辑应用，退出不会保存\n确定退出？", "取消", null, "退出", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        backConfirm();
    }
}
