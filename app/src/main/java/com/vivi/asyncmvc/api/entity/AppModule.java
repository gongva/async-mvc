package com.vivi.asyncmvc.api.entity;

import android.app.Activity;
import android.studio.os.PreferencesUtils;
import android.studio.plugins.GsonUtils;

import com.google.gson.reflect.TypeToken;
import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.event.SaveMyModulesEvent;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.ui.home.appmodule.AppModuleActivity;
import com.vivi.asyncmvc.ui.home.clicense.CLicenseCategoryActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * App功能模块，eg：驾驶证审验
 *
 * @author gongwei
 * @Date 2019/1/14
 */
public class AppModule implements Serializable {

    private static final String PREFER_KEY_USER_MODULE = "prefer_key_user_module";

    public AppModuleType module;

    private static List<AppModule> myModules;//我的应用
    private static List<AppModule> driverLicenseModules;//驾驶证业务
    private static List<AppModule> carLicenseModules;//行驶证业务
    private static List<AppModule> nearbyModules;//出行周边业务

    public AppModule(AppModuleType module) {
        this.module = module;
    }

    /**
     * 获取我的应用Modules
     * 系统默认：违法信息、交通信用、汽车摇号、违法举报、车险评估、文明保修、一键挪车
     *
     * @return
     */
    public static List<AppModule> getMyModules() {
        if (myModules == null) {
            myModules = GsonUtils.jsonDeserializer(PreferencesUtils.getString(PREFER_KEY_USER_MODULE, ""), new TypeToken<ArrayList<AppModule>>() {
            });
        }
        if (myModules == null) {
            myModules = new ArrayList<>();
            myModules.add(new AppModule(AppModuleType.MODULE_LAW));
            myModules.add(new AppModule(AppModuleType.MODULE_CREDIT));
            myModules.add(new AppModule(AppModuleType.MODULE_RANDOM));
            myModules.add(new AppModule(AppModuleType.MODULE_REPORT));
            myModules.add(new AppModule(AppModuleType.MODULE_INSURANCE));
            myModules.add(new AppModule(AppModuleType.MODULE_FIX));
            myModules.add(new AppModule(AppModuleType.MODULE_MOVE));
        }
        return myModules;
    }

    /**
     * 保存我的应用
     *
     * @param modules
     */
    public static void saveMyModules(List<AppModule> modules) {
        PreferencesUtils.setString(PREFER_KEY_USER_MODULE, GsonUtils.jsonSerializer(myModules));
        myModules = modules;
        BusProvider.post(new SaveMyModulesEvent());
    }

    /**
     * 获取驾驶证业务Modules
     *
     * @return
     */
    public static List<AppModule> getDriverLicenseModules() {
        if (driverLicenseModules == null) {
            driverLicenseModules = new ArrayList<>();
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_VERIFY));
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_ADDRESS_MODIFY));
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_PHONE_MODIFY));
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_OUT_OF_DATE));
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_EXPIRED));
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_LOST));
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_OVERAGE));
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_BROKEN));
            driverLicenseModules.add(new AppModule(AppModuleType.MODULE_CREDIT));
        }
        return driverLicenseModules;
    }

    /**
     * 获取行驶证（机动车）业务Modules
     *
     * @return
     */
    public static List<AppModule> getCarLicenseModules() {
        if (carLicenseModules == null) {
            carLicenseModules = new ArrayList<>();
            carLicenseModules.add(new AppModule(AppModuleType.MODULE_RANDOM));
            carLicenseModules.add(new AppModule(AppModuleType.MODULE_INSURANCE));
            carLicenseModules.add(new AppModule(AppModuleType.MODULE_CHECK));
            carLicenseModules.add(new AppModule(AppModuleType.MODULE_SIGN));
            carLicenseModules.add(new AppModule(AppModuleType.MODULE_REGET));
            carLicenseModules.add(new AppModule(AppModuleType.MODULE_CAR_LICENSE));
            carLicenseModules.add(new AppModule(AppModuleType.MODULE_LAW));
        }
        return carLicenseModules;
    }

    /**
     * 获取出行周边业务Modules
     *
     * @return
     */
    public static List<AppModule> getNearbyModules() {
        if (nearbyModules == null) {
            nearbyModules = new ArrayList<>();
            nearbyModules.add(new AppModule(AppModuleType.MODULE_44));
            nearbyModules.add(new AppModule(AppModuleType.MODULE_MOVE));
            nearbyModules.add(new AppModule(AppModuleType.MODULE_FIX));
            nearbyModules.add(new AppModule(AppModuleType.MODULE_REPORT));
            nearbyModules.add(new AppModule(AppModuleType.MODULE_TRAFFIC));
            nearbyModules.add(new AppModule(AppModuleType.MODULE_ACCIDENT));
        }
        return nearbyModules;
    }

    /**
     * 判断list中是否包含appModule
     *
     * @param list
     * @param appModule
     * @return
     */
    private static boolean contains(List<AppModule> list, AppModule appModule) {
        if (list == null || appModule == null) return false;
        for (AppModule m : list) {
            if (m.module == appModule.module) return true;
        }
        return false;
    }

    /**
     * 删除list中某个Module
     *
     * @param list
     * @param appModule
     */
    public static void removeModule(List<AppModule> list, AppModule appModule) {
        if (list != null && appModule != null) {
            for (AppModule m : list) {
                if (m.module == appModule.module) {
                    list.remove(m);
                    return;
                }
            }
        }
    }

    /**
     * 判断参数appModule是否在“我的应用”中
     *
     * @param appModule
     * @return
     */
    public static boolean isImMyModules(AppModule appModule) {
        return contains(getMyModules(), appModule);
    }

    /**
     * 所有业务功能点击时的跳转方法
     *
     * @param context
     * @param appModule
     */
    public static void startToModuleActivity(Activity context, AppModule appModule) {
        switch (appModule.module) {
            //驾驶证业务
            case MODULE_VERIFY:
                break;
            case MODULE_ADDRESS_MODIFY:
                break;
            case MODULE_PHONE_MODIFY:
                break;
            case MODULE_OUT_OF_DATE:
                break;
            case MODULE_EXPIRED:
                break;
            case MODULE_LOST:
                break;
            case MODULE_OVERAGE:
                break;
            case MODULE_BROKEN:
                break;
            case MODULE_CREDIT:
                break;
            //驾驶证业务
            case MODULE_RANDOM:
                break;
            case MODULE_INSURANCE:
                break;
            case MODULE_CHECK:
                break;
            case MODULE_SIGN:
                break;
            case MODULE_REGET:
                break;
            case MODULE_CAR_LICENSE:
                CLicenseCategoryActivity.start(context);
                break;
            case MODULE_LAW:
                break;
            //出行周边
            case MODULE_44:
                break;
            case MODULE_MOVE:
                break;
            case MODULE_FIX:
                break;
            case MODULE_REPORT:
                break;
            case MODULE_TRAFFIC:
                break;
            case MODULE_ACCIDENT:
                break;
            //更多
            case MODULE_MORE:
                AppModuleActivity.start(context);
                break;
        }
    }

    public enum AppModuleType implements Serializable {
        MODULE_VERIFY("驾驶证审验", R.drawable.ic_module_verify),
        MODULE_ADDRESS_MODIFY("变更驾驶证\n地址", R.drawable.ic_module_address_modify),
        MODULE_PHONE_MODIFY("变更驾驶证\n联系方式", R.drawable.ic_module_phone_modify),
        MODULE_OUT_OF_DATE("延期换领\n驾驶证", R.drawable.ic_module_out_of_date),
        MODULE_EXPIRED("期满换领\n驾驶证", R.drawable.ic_module_expired),
        MODULE_LOST("遗失补领\n驾驶证", R.drawable.ic_module_lost),
        MODULE_OVERAGE("超龄换领\n驾驶证", R.drawable.ic_module_overage),
        MODULE_BROKEN("损毁换领\n驾驶证", R.drawable.ic_module_broken),
        MODULE_CREDIT("交通信用", R.drawable.ic_module_credit),

        MODULE_RANDOM("汽车摇号", R.drawable.ic_module_random),
        MODULE_INSURANCE("车险评估", R.drawable.ic_module_insurance),
        MODULE_CHECK("机动车查验\n预约", R.drawable.ic_module_check),
        MODULE_SIGN("申领免检\n合格标志", R.drawable.ic_module_sign),
        MODULE_REGET("补证换牌", R.drawable.ic_module_reget),
        MODULE_CAR_LICENSE("备案机动车", R.drawable.ic_module_car_license),
        MODULE_LAW("违法信息", R.drawable.ic_module_law),

        MODULE_44("开四停四", R.drawable.ic_module_44),
        MODULE_MOVE("一键挪车", R.drawable.ic_module_move),
        MODULE_FIX("文明报修", R.drawable.ic_module_fix),
        MODULE_REPORT("违法举报", R.drawable.ic_module_report),
        MODULE_TRAFFIC("实时路况", R.drawable.ic_module_traffic),
        MODULE_ACCIDENT("快处快赔", R.drawable.ic_module_accident),

        MODULE_MORE("更多", R.drawable.ic_module_more);

        public String name;
        public int icRes;

        AppModuleType(String name, int icRes) {
            this.name = name;
            this.icRes = icRes;
        }
    }
}
