package com.vivi.asyncmvc.comm.managers;

import android.studio.os.PreferencesUtils;
import android.studio.plugins.GsonUtils;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.LoginInfo;
import com.vivi.asyncmvc.api.entity.UserInfo;
import com.vivi.asyncmvc.library.plugs.alipush.AliPushConfig;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.comm.MainPageActivity;

/**
 * 用户登录管理器
 *
 * @author gongwei
 * @date 2019.1.18
 */
public class LoginManager {

    public final String Pref_LoginUser = "login_user";
    public final String Pref_loginAccount = "login_account";

    private static LoginManager INSTANCE = null;

    protected String loginAccount;
    protected LoginInfo loginInfo;

    public synchronized static LoginManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LoginManager();
            INSTANCE.initConfig();
        }
        return INSTANCE;
    }

    protected void initConfig() {
        loginAccount = PreferencesUtils.getString(Pref_loginAccount, null);

        String jsonConfig = PreferencesUtils.getString(Pref_LoginUser, null);
        loginInfo = GsonUtils.jsonDeserializer(jsonConfig, LoginInfo.class);
    }

    public void saveAccount(String loginAccount) {
        this.loginAccount = loginAccount;
        save();
    }

    public void saveUserInfo(UserInfo userInfo) {
        if (this.loginInfo != null) {
            this.loginInfo.userInfo = userInfo;
            save();
        }
    }

    public void saveLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
        save();
    }

    public void save(String loginAccount, LoginInfo loginInfo) {
        this.loginAccount = loginAccount;
        this.loginInfo = loginInfo;
        save();
    }

    public void save() {
        PreferencesUtils.setString(Pref_loginAccount, loginAccount);
        PreferencesUtils.setString(Pref_LoginUser, GsonUtils.jsonSerializer(loginInfo));
    }

    /**
     * 是否已登录
     *
     * @return
     */
    public boolean isLogin() {
        return INSTANCE.loginInfo != null;
    }

    /**
     * 获取用户ID
     *
     * @return userId or null
     */
    public String getUserId() {
        if (loginInfo != null && loginInfo.userInfo != null) {
            return loginInfo.userInfo.id;
        }
        return null;
    }

    /**
     * 获取用户信息
     *
     * @return userInfo or null
     */
    public UserInfo getUserInfo() {
        if (loginInfo != null) {
            return loginInfo.userInfo;
        }
        return null;
    }

    /**
     * 获取用户token
     *
     * @return token
     */
    public String getToken() {
        if (loginInfo != null) {
            return loginInfo.token;
        }
        return null;
    }

    /**
     * 获取登录账号
     *
     * @return loginAccount
     */
    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    /**
     * 登录
     *
     * @param account
     * @param password
     * @param callback
     */
    public static void login(final String account, final String password, final LoginCallback callback) {
        Api.login(account, password, new JsonResultCallback<JsonResult<LoginInfo>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<LoginInfo> response, int tag) {
                getInstance().save(account, response.getData());
                AliPushConfig.turnOnPushChannel();
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                if (callback != null) {
                    callback.onError(statusCode, responseString);
                }
            }
        });
    }

    /**
     * 注销，无需传参，header的token里带了用户id
     *
     */
    public static void logout() {
        Api.logout(null);
    }

    /**
     * 退出账号
     */
    public void exitAccount() {
        saveLoginInfo(null);
        AliPushConfig.turnOffPushChannel();
    }

    public interface LoginCallback {
        void onSuccess();

        void onError(int errCode, String response);
    }
}
