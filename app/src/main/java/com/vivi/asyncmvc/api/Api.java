package com.vivi.asyncmvc.api;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.vivi.asyncmvc.api.entity.Address;
import com.vivi.asyncmvc.api.entity.Area;
import com.vivi.asyncmvc.api.entity.article.Article;
import com.vivi.asyncmvc.api.entity.article.ArticleCategory;
import com.vivi.asyncmvc.api.entity.BusinessType;
import com.vivi.asyncmvc.api.entity.CarLicenseBindRecord;
import com.vivi.asyncmvc.api.entity.Dict;
import com.vivi.asyncmvc.api.entity.DriverLicenseBindCheck;
import com.vivi.asyncmvc.api.entity.GetCaptcha;
import com.vivi.asyncmvc.api.entity.HomeCard;
import com.vivi.asyncmvc.api.entity.IdEntity;
import com.vivi.asyncmvc.api.entity.LoginInfo;
import com.vivi.asyncmvc.api.entity.CaptchaCheck;
import com.vivi.asyncmvc.api.entity.Message;
import com.vivi.asyncmvc.api.entity.UnreadMessageCount;
import com.vivi.asyncmvc.api.entity.UrlEntity;
import com.vivi.asyncmvc.api.entity.UserInfo;
import com.vivi.asyncmvc.api.entity.Version;
import com.vivi.asyncmvc.api.entity.VersionLog;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.SystemConfig;
import com.vivi.asyncmvc.library.plugs.http.AHttpRequest;
import com.vivi.asyncmvc.library.plugs.http.JsonRequestParams;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultList;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultRow;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultTsList;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.OS;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

public class Api {

    /**
     * 版本更新
     *
     * @param callback
     */
    public static void versionCheck(JsonResultCallback<JsonResult<Version>> callback) {
        AHttpRequest.get(AppContext.VERSION_CHECK, null, callback);
    }

    /**
     * 获取系统配置
     *
     * @param callback
     */
    public static void getSysConfig(JsonResultCallback<JsonResult<SystemConfig>> callback) {
        AHttpRequest.get(AppContext.GET_SYS_CONFIG, null, callback);
    }

    /**
     * 获取系统字典
     *
     * @param dictType 字典类型
     * @param callback
     */
    public static void getDictList(String dictType, JsonResultCallback<JsonResultRow<Dict>> callback) {
        RequestParams params = new RequestParams();
        params.put("dictType", dictType);
        AHttpRequest.get(AppContext.DICT_LIST, params, callback);
    }

    /**
     * 登录
     *
     * @param account  登录账号（手机号）
     * @param password
     * @param callback
     */
    public static void login(String account, String password, JsonResultCallback<JsonResult<LoginInfo>> callback) {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        RequestParams params = new RequestParams();
        params.put("account", account);
        params.put("password", password);
        params.put("deviceId", pushService.getDeviceId());//在客户端调用阿里云移动推送方法 getDeviceld获得设备号
        params.put("deviceName", OS.getDeviceName());
        AHttpRequest.post(AppContext.LOGIN, params, callback);
    }

    /**
     * 获取用户信息
     *
     * @param callback
     */
    public static void getUserInfo(JsonResultCallback<JsonResult<UserInfo>> callback) {
        AHttpRequest.get(AppContext.GET_USER_INFO, null, callback);
    }

    /**
     * 注销
     *
     * @param callback
     */
    public static void logout(JsonResultCallback<JsonResultVoid> callback) {
        AHttpRequest.post(AppContext.LOGOUT, null, callback);
    }

    /**
     * 获取注册协议地址
     *
     * @param callback
     */
    public static void getRegisterProtocol(JsonResultCallback<JsonResult<UrlEntity>> callback) {
        AHttpRequest.get(AppContext.GET_REGISTER_PROTOCOL, null, callback);
    }

    /**
     * 注册：获取注册验证码
     * 忘记密码：获取注册验证码
     *
     * @param action：动作类型 register：注册验证码，forgetPassword：忘记密码获取验证码
     * @param phone
     * @param callback
     */
    public static void getCaptcha(String action, String phone, JsonResultCallback<JsonResult<GetCaptcha>> callback) {
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        switch (action) {
            case "register":
                AHttpRequest.get(AppContext.GET_REGISTER_CAPTCHA, params, callback);
                break;
            case "forgetPassword":
                AHttpRequest.get(AppContext.GET_FORGET_PASSWORD_CAPTCHA, params, callback);
                break;
        }
    }

    /**
     * 注册：验证 注册短信验证码 是否正确
     * 忘记密码：验证 注册短信验证码 是否正确
     *
     * @param action：动作类型 register：注册验证码，forgetPassword：忘记密码获取验证码
     * @param phone
     * @param vCode
     * @param callback
     */
    public static void verifyCaptcha(String action, String phone, String vCode, JsonResultCallback<JsonResult<CaptchaCheck>> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("phone", phone);
        params.put("vCode", vCode);
        switch (action) {
            case "register":
                AHttpRequest.post(AppContext.VERIFY_REGISTER_CAPTCHA, params, callback);
                break;
            case "forgetPassword":
                AHttpRequest.post(AppContext.VERIFY_FORGET_PASSWORD_CAPTCHA, params, callback);
                break;
        }
    }

    /**
     * 注册-提交
     * 忘记密码-修改密码
     *
     * @param action：动作类型 register：注册验证码，forgetPassword：忘记密码获取验证码
     * @param phone
     * @param password
     * @param token       验证注册验证码时返回的token字段
     * @param callback
     */
    public static void passwordCommit(String action, String phone, String password, String token, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("phone", phone);
        params.put("password", password);
        params.put("token", token);
        switch (action) {
            case "register":
                AHttpRequest.post(AppContext.REGISTER, params, callback);
                break;
            case "forgetPassword":
                AHttpRequest.post(AppContext.FORGET_PASSWORD_RESET, params, callback);
                break;
        }
    }

    /**
     * 忘记密码：验证 关联的电子驾照
     *
     * @param phone
     * @param realName
     * @param idCardNum 身份证号
     * @param dlCode    驾驶证档案编号
     * @param callback
     */
    public static void verifyForgetPasswordLicense(String phone, String realName, String idCardNum, String dlCode, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("phone", phone);
        params.put("realName", realName);
        params.put("idCardNum", idCardNum);
        params.put("dlCode", dlCode);
        AHttpRequest.post(AppContext.VERIFY_FORGET_PASSWORD_LICENSE, params, callback);
    }

    /**
     * 修改邮箱
     *
     * @param email
     * @param callback
     */
    public static void changeEmail(String email, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("email", email);
        AHttpRequest.post(AppContext.CHANGE_EMAIL, params, callback);
    }

    /**
     * 修改手机号-获取验证码
     *
     * @param phone
     * @param password
     * @param callback
     */
    public static void getCaptchaChangePhone(String phone, String password, JsonResultCallback<JsonResultVoid> callback) {
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("password", password);
        AHttpRequest.get(AppContext.GET_CHANGE_PHONE_CAPTCHA, params, callback);
    }

    /**
     * 修改手机号-提交
     *
     * @param phone
     * @param vCode
     * @param callback
     */
    public static void changePhone(String phone, String vCode, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("phone", phone);
        params.put("vCode", vCode);
        AHttpRequest.post(AppContext.CHANGE_PHONE, params, callback);
    }

    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     * @param callback
     */
    public static void changePassword(String oldPassword, String newPassword, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("oldPassword", oldPassword);
        params.put("newPassword", newPassword);
        AHttpRequest.post(AppContext.CHANGE_PASSWORD, params, callback);
    }

    /**
     * 获取收货地址列表
     *
     * @param callback
     */
    public static void addressList(JsonResultCallback<JsonResultRow<Address>> callback) {
        AHttpRequest.get(AppContext.ADDRESS_LIST, null, callback);
    }

    /**
     * 新增收货地址
     *
     * @param address
     * @param callback
     */
    public static void addressAdd(Address address, JsonResultCallback<JsonResult<IdEntity>> callback) {
        if (address != null) {
            JsonRequestParams params = new JsonRequestParams();
            params.put("province", address.province);
            params.put("provinceCode", address.provinceCode);
            params.put("city", address.city);
            params.put("cityCode", address.cityCode);
            params.put("county", address.county);
            params.put("countyCode", address.countyCode);
            params.put("address", address.address);
            params.put("recipientName", address.recipientName);
            params.put("recipientPhone", address.recipientPhone);
            params.put("isDefault", address.isDefault);
            AHttpRequest.post(AppContext.ADDRESS_ADD, params, callback);
        }
    }

    /**
     * 修改收货地址
     *
     * @param address
     * @param callback
     */
    public static void addressUpdate(Address address, JsonResultCallback<JsonResultVoid> callback) {
        if (address != null) {
            JsonRequestParams params = new JsonRequestParams();
            params.put("id", address.id);
            params.put("province", address.province);
            params.put("provinceCode", address.provinceCode);
            params.put("city", address.city);
            params.put("cityCode", address.cityCode);
            params.put("county", address.county);
            params.put("countyCode", address.countyCode);
            params.put("address", address.address);
            params.put("recipientName", address.recipientName);
            params.put("recipientPhone", address.recipientPhone);
            params.put("isDefault", address.isDefault);
            AHttpRequest.post(AppContext.ADDRESS_UPDATE, params, callback);
        }
    }

    /**
     * 删除收货地址
     *
     * @param id
     * @param callback
     */
    public static void addressDelete(String id, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("id", id);
        AHttpRequest.post(AppContext.ADDRESS_DELETE, params, callback);
    }

    /**
     * 设为默认
     *
     * @param id
     * @param callback
     */
    public static void addressDefault(String id, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("id", id);
        AHttpRequest.post(AppContext.ADDRESS_DEFAULT, params, callback);
    }

    /**
     * 获取省市区字典数据：省/市/区县
     *
     * @param code     获取省时传null
     * @param callback
     */
    public static void getArea(String code, JsonResultCallback<JsonResultRow<Area>> callback) {
        RequestParams params = new RequestParams();
        params.put("code", code);
        AHttpRequest.get(AppContext.GET_AREA, params, callback);
    }

    /**
     * 获取App版本历史
     *
     * @param page
     * @param callback
     */
    public static void appVersionLog(int page, JsonResultCallback<JsonResultList<VersionLog>> callback) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("size", AppConfig.HTTP_DEFAULT_SIZE);
        AHttpRequest.get(AppContext.APP_VERSION_LOG, params, callback);
    }

    /**
     * 图片上传
     *
     * @param file
     * @param callback
     * @return
     */
    public static RequestHandle uploadImage(File file, JsonResultCallback<JsonResult<UrlEntity>> callback) {
        RequestParams params = new RequestParams();
        try {
            params.put("image", file);
            return AHttpRequest.post(AppContext.UPLOAD_IMAGE, params, callback);
        } catch (FileNotFoundException e) {
            if (callback != null) {
                callback.onFailure(400, e.getMessage(), e, 0);
            }
            return null;
        }
    }

    /**
     * 更新头像
     *
     * @param avatar
     * @param callback
     */
    public static void updateHead(String avatar, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("avatar", avatar);
        AHttpRequest.post(AppContext.UPDATE_HEAD, params, callback);
    }

    /**
     * 意见反馈
     *
     * @param content  反馈内容，不超过200个字符
     * @param pics     图片链接，以英文逗号分割
     * @param callback
     */
    public static void feedBack(String content, String pics, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("content", content);
        params.put("pics", pics);
        AHttpRequest.post(AppContext.FEEDBACK, params, callback);
    }

    /**
     * 绑定驾照-身份证检查
     *
     * @param realName
     * @param idCard
     * @param callback
     */
    public static void driverLicenseBindCheck(String realName, String idCard, JsonResultCallback<JsonResult<DriverLicenseBindCheck>> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("realName", realName);
        params.put("idCard", idCard);
        AHttpRequest.post(AppContext.DRIVER_LICENSE_BIND_CHECK, params, callback);
    }

    /**
     * 驾驶证绑定-提交
     *
     * @param realName
     * @param idCard
     * @param fileNum  驾驶证档案号
     * @param callback
     */
    public static void driverLicenseBind(String realName, String idCard, String fileNum, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("realName", realName);
        params.put("idCard", idCard);
        params.put("fileNum", fileNum);
        AHttpRequest.post(AppContext.DRIVER_LICENSE_BIND, params, callback);
    }

    /**
     * 获取首页卡片信息
     *
     * @param callback
     */
    public static void getHomeCard(JsonResultCallback<JsonResult<HomeCard>> callback) {
        AHttpRequest.get(AppContext.HOME_CARD, null, callback);
    }

    /**
     * 绑定行驶证-发送验证码
     *
     * @param plateType 号牌种类Code
     * @param plateNum  车牌号
     * @param engineNum 发动机号后六位
     * @param owner     车主姓名
     * @param callback
     */
    public static void getCaptchaCarLicenseBind(String plateType, String plateNum, String engineNum, String owner, JsonResultCallback<JsonResult<HomeCard>> callback) {
        RequestParams params = new RequestParams();
        params.put("plateType", plateType);
        params.put("plateNum", plateNum);
        params.put("engineNum", engineNum);
        params.put("owner", owner);
        AHttpRequest.get(AppContext.GET_CAR_LICENSE_BIND_CAPTCHA, params, callback);
    }

    /**
     * 绑定行驶证-提交
     *
     * @param plateType
     * @param plateNum
     * @param engineNum
     * @param owner
     * @param vCode
     * @param callback
     */
    public static void carLicenseBind(String plateType, String plateNum, String engineNum, String owner, String vCode, JsonResultCallback<JsonResult<HomeCard>> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("plateType", plateType);
        params.put("plateNum", plateNum);
        params.put("engineNum", engineNum);
        params.put("owner", owner);
        params.put("vCode", vCode);
        AHttpRequest.post(AppContext.CAR_LICENSE_BIND, params, callback);
    }

    /**
     * 用户备案机动车记录
     *
     * @param callback
     */
    public static void getCarLicenseBindRecord(JsonResultCallback<JsonResultRow<CarLicenseBindRecord>> callback) {
        AHttpRequest.get(AppContext.CAR_LICENSE_BIND_RECORD, null, callback);
    }

    /**
     * 机动车被他人备案记录
     *
     * @param plateNum
     * @param callback
     */
    public static void getCarLicenseBindRecordOther(String plateNum, JsonResultCallback<JsonResultRow<CarLicenseBindRecord>> callback) {
        RequestParams params = new RequestParams();
        params.put("plateNum", plateNum);
        AHttpRequest.get(AppContext.CAR_LICENSE_BIND_RECORD_OTHER, params, callback);
    }

    /**
     * 解绑机动车备案
     *
     * @param id
     * @param callback
     */
    public static void carLicenseUnbind(String id, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("id", id);
        AHttpRequest.post(AppContext.CAR_LICENSE_UNBIND, params, callback);
    }

    /**
     * 温馨服务-获取问题类型
     *
     * @param callback
     */
    public static void getBusinessType(JsonResultCallback<JsonResultRow<BusinessType>> callback) {
        AHttpRequest.get(AppContext.GET_BUSINESS_TYPE, null, callback);
    }

    /**
     * 温馨服务-问题提交
     *
     * @param businessTypeCode
     * @param businessTypeName
     * @param questionTypeCode
     * @param questionTypeName
     * @param description
     * @param callback
     */
    public static void questionCommit(String businessTypeCode, String businessTypeName, String questionTypeCode, String questionTypeName, String description, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("businessTypeCode", businessTypeCode);
        params.put("businessTypeName", businessTypeName);
        params.put("questionTypeCode", questionTypeCode);
        params.put("questionTypeName", questionTypeName);
        params.put("description", description);
        AHttpRequest.post(AppContext.QUESTION_COMMIT, params, callback);
    }

    /**
     * 获取消息列表
     *
     * @param isSmart   是否是首页的智慧服务
     * @param timestamp
     * @param callback
     */
    public static void getMessageList(boolean isSmart, long timestamp, JsonResultCallback<JsonResultTsList<Message>> callback) {
        RequestParams params = new RequestParams();
        params.put("isSmart", isSmart);
        params.put("timestamp", timestamp);
        params.put("size", AppConfig.HTTP_DEFAULT_SIZE);
        AHttpRequest.get(AppContext.GET_MESSAGE_LIST, params, callback);
    }

    /**
     * 获取未读消息数
     *
     * @param callback
     */
    public static void getUnreadMessageCount(JsonResultCallback<JsonResult<UnreadMessageCount>> callback) {
        AHttpRequest.get(AppContext.GET_UNREAD_MESSAGE_COUNT, null, callback);
    }

    /**
     * 读消息(设为已读)
     *
     * @param id
     * @param callback
     */
    public static void messageRead(String id, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("id", id);
        AHttpRequest.post(AppContext.MESSAGE_READ, params, callback);
    }

    /**
     * 删除消息
     *
     * @param id
     * @param callback
     */
    public static void messageDelete(String id, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("id", id);
        AHttpRequest.post(AppContext.MESSAGE_DELETE, params, callback);
    }

    /**
     * 消息-设置首页不展示
     *
     * @param id
     * @param callback
     */
    public static void messageHomeNotShow(String id, JsonResultCallback<JsonResultVoid> callback) {
        JsonRequestParams params = new JsonRequestParams();
        params.put("id", id);
        AHttpRequest.post(AppContext.MESSAGE_HOME_NOT_SHOW, params, callback);
    }

    /**
     * 安全行驶-获取一级列表
     *
     * @param callback
     */
    public static void getArticleCategory(JsonResultCallback<JsonResultRow<ArticleCategory>> callback) {
        AHttpRequest.get(AppContext.getArticleCategory, null, callback);
    }

    /**
     * 安全行驶-获取二级列表
     *
     * @param type     类型标识 @see ArticleCategory.type
     * @param page
     * @param callback
     */
    public static void getArticleList(String type, int page, JsonResultCallback<JsonResultList<Article>> callback) {
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("page", page);
        params.put("size", AppConfig.HTTP_DEFAULT_SIZE);
        AHttpRequest.get(AppContext.getArticleList, params, callback);
    }

    /**
     * 安全行驶-获取文章详情
     *
     * @param id
     * @param callback
     */
    public static void getArticleDetail(String id, JsonResultCallback<JsonResult<Article>> callback) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        AHttpRequest.get(AppContext.getArticleDetail, params, callback);
    }
}