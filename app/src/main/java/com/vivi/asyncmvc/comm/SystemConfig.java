package com.vivi.asyncmvc.comm;

import android.studio.os.PreferencesUtils;
import android.studio.plugins.GsonUtils;
import android.studio.util.DateUtils;
import android.text.TextUtils;

import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;

import java.util.Date;

/**
 * 系统配置信息
 *
 * @author gongwei 2019.1.5
 */
public class SystemConfig {

    private static final String SysConfigPref = "sys_config_pref";

    private static SystemConfig instance;

    //本地时间
    private long localTime;
    //服务器时间
    private long systemTimestamp;
    //图片前缀地址
    private String imageUrlPrefix;

    public static SystemConfig getInstance() {
        if (instance == null) {
            instance = new SystemConfig();
            initConfigPref();
        }
        return instance;
    }

    private static void initConfigPref() {
        instance.systemTimestamp = System.currentTimeMillis();
        instance.localTime = System.currentTimeMillis();
        String jsonConfig = PreferencesUtils.getString(SysConfigPref, null);
        if (jsonConfig != null) {
            SystemConfig systemConfig = GsonUtils.jsonDeserializer(jsonConfig, SystemConfig.class);
            if (systemConfig != null) {
                instance.updateConfig(systemConfig);
            }
        }
    }


    /**
     * 更新配置到文件
     *
     * @param config
     */
    public void updateConfig(SystemConfig config) {
        systemTimestamp = config.systemTimestamp;
        localTime = config.localTime;
        if (!TextUtils.isEmpty(config.imageUrlPrefix)) {
            AppContext.BASE_URL_IMAGE = config.imageUrlPrefix;
        }
    }

    /**
     * 更新系统配置
     */
    public static void updateConfig() {
        Api.getSysConfig(new JsonResultCallback<JsonResult<SystemConfig>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<SystemConfig> response, int tag) {
                SystemConfig config = response.getData();
                config.localTime = System.currentTimeMillis();
                getInstance().updateConfig(config);
            }
        });
    }

    /**
     * 利用服务器和本地时间的差值，计算当前的获取服务器时间
     *
     * @return long
     */
    public long getServerTimeLong() {
        return systemTimestamp + System.currentTimeMillis() - localTime;
    }

}
