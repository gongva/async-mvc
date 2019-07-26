package com.vivi.asyncmvc.comm;

/**
 * 接口地址定义
 *
 * @author gongwei 2018/12/18
 */
public abstract class AppContext {

    public static final boolean isDebug = true;

    public static final String BASE_URL_HTTP = "http://10.197.236.45:9001";//接口前缀-赵东
    //public static final String BASE_URL_HTTP = "http://10.197.236.22:9001";//接口前缀-邦凯
    //public static final String BASE_URL_HTTP = "http://10.197.236.46:9001";//接口前缀-张付兵说是邦凯的电脑
    //public static final String BASE_URL_HTTP = "http://10.197.236.151:3000/mock/11";//接口前缀-YApi
    public static String BASE_URL_IMAGE = "http://10.197.236.26:8666";//图片地址前缀


    /************公共模块***********/
    //检查更新 get
    public static final String VERSION_CHECK = BASE_URL_HTTP + "/app/sys/version/update";
    //获取系统配置 get
    public static final String GET_SYS_CONFIG = BASE_URL_HTTP + "/sys/config";
    //获取系统字典 get
    public static final String DICT_LIST = BASE_URL_HTTP + "/sys/dict/list";

    /************用户模块***********/
    //登录 post
    public static final String LOGIN = BASE_URL_HTTP + "/user/login";
    //注销 post
    public static final String LOGOUT = BASE_URL_HTTP + "/user/logout";
    //注册：获得 协议 get
    public static final String GET_REGISTER_PROTOCOL = BASE_URL_HTTP + "/app/protocol/detail";
    //注册：获得 验证码 get
    public static final String GET_REGISTER_CAPTCHA = BASE_URL_HTTP + "/user/register/vCode/detail";
    //注册：验证 验证码 post
    public static final String VERIFY_REGISTER_CAPTCHA = BASE_URL_HTTP + "/user/register/vCode";
    //注册：提交 post
    public static final String REGISTER = BASE_URL_HTTP + "/user/register";
    //忘记密码：获得 验证码 get
    public static final String GET_FORGET_PASSWORD_CAPTCHA = BASE_URL_HTTP + "/user/forgetPassword/vCode/detail";
    //忘记密码：验证 验证码 post
    public static final String VERIFY_FORGET_PASSWORD_CAPTCHA = BASE_URL_HTTP + "/user/forgetPassword/vCode";
    //忘记密码：验证 验证关联的电子驾照 post
    public static final String VERIFY_FORGET_PASSWORD_LICENSE = BASE_URL_HTTP + "/user/forgetPassword/driverLicense";
    //忘记密码：重置密码 post
    public static final String FORGET_PASSWORD_RESET = BASE_URL_HTTP + "/user/forgetPassword/resetPwd";

    /************首页***********/
    //驾驶证绑定-校验身份证 post
    public static final String DRIVER_LICENSE_BIND_CHECK = BASE_URL_HTTP + "/app/driverLicense/valid";
    //驾驶证绑定-提交 post
    public static final String DRIVER_LICENSE_BIND = BASE_URL_HTTP + "/app/driverLicense/bind";
    //获取首页卡片信息 get
    public static final String HOME_CARD = BASE_URL_HTTP + "/app/index/card";
    //绑定行驶证-获取验证码 get
    public static final String GET_CAR_LICENSE_BIND_CAPTCHA = BASE_URL_HTTP + "/app/vehicle/vCode/detail";
    //绑定行驶证-提交 post
    public static final String CAR_LICENSE_BIND = BASE_URL_HTTP + "/app/vehicle/bind";
    //绑定行驶证-用户备案机动车记录 get
    public static final String CAR_LICENSE_BIND_RECORD = BASE_URL_HTTP + "/app/vehicle/history";
    //绑定行驶证-机动车被他人备案记录 get
    public static final String CAR_LICENSE_BIND_RECORD_OTHER = BASE_URL_HTTP + "/app/vehicle/bind/history";
    //绑定行驶证-解绑机动车备案 post
    public static final String CAR_LICENSE_UNBIND = BASE_URL_HTTP + "/app/vehicle/unbind";
    //温馨服务-获取问题类型 get
    public static final String GET_BUSINESS_TYPE = BASE_URL_HTTP + "/app/warm/questionType/list";
    //温馨服务-提交 post
    public static final String QUESTION_COMMIT = BASE_URL_HTTP + "/app/warm/question/save";
    //获取安全行驶一级列表 get
    public static final String getArticleCategory = BASE_URL_HTTP + "/app/safedrive/first";
    //获取安全行驶二级列表 get
    public static final String getArticleList = BASE_URL_HTTP + "/app/safedrive/second";
    //安全行驶详情 get
    public static final String getArticleDetail = BASE_URL_HTTP + "/app/safedrive/detail";

    /************消息中心***********/
    //【消息中心】获取消息列表 get
    public static final String GET_MESSAGE_LIST = BASE_URL_HTTP + "/msg/list";
    //【消息中心】获取未读消息数 get
    public static final String GET_UNREAD_MESSAGE_COUNT = BASE_URL_HTTP + "/msg/count/unRead";
    //【消息中心】读消息(设为已读) post
    public static final String MESSAGE_READ = BASE_URL_HTTP + "/msg/read";
    //【消息中心】删除消息 post
    public static final String MESSAGE_DELETE = BASE_URL_HTTP + "/msg/delete";
    //【消息中心】首页不展示 post
    public static final String MESSAGE_HOME_NOT_SHOW = BASE_URL_HTTP + "/msg/homeNotShow";

    /************个人中心***********/
    //获取用户个人信息 get
    public static final String GET_USER_INFO = BASE_URL_HTTP + "/user/info";
    //修改邮箱 post
    public static final String CHANGE_EMAIL = BASE_URL_HTTP + "/user/email";
    //修改手机号-获取验证码 get
    public static final String GET_CHANGE_PHONE_CAPTCHA = BASE_URL_HTTP + "/user/phone/vCode";
    //修改手机号-提交 post
    public static final String CHANGE_PHONE = BASE_URL_HTTP + "/user/phone";
    //修改密码 post
    public static final String CHANGE_PASSWORD = BASE_URL_HTTP + "/user/password/update";
    //收货地址-列表 get
    public static final String ADDRESS_LIST = BASE_URL_HTTP + "/user/address/list";
    //收货地址-新增 post
    public static final String ADDRESS_ADD = BASE_URL_HTTP + "/user/address/add";
    //收货地址-修改 post
    public static final String ADDRESS_UPDATE = BASE_URL_HTTP + "/user/address/update";
    //收货地址-删除 post
    public static final String ADDRESS_DELETE = BASE_URL_HTTP + "/user/address/delete";
    //收货地址-设为默认 post
    public static final String ADDRESS_DEFAULT = BASE_URL_HTTP + "/user/address/default";
    //获取省市区字典数据 get
    public static final String GET_AREA = BASE_URL_HTTP + "/sys/area";
    //历史版本介绍 get
    public static final String APP_VERSION_LOG = BASE_URL_HTTP + "/app/changelog";
    //上传图片 post
    public static final String UPLOAD_IMAGE = BASE_URL_HTTP + "/sys/image/upload";
    //修改头像 post
    public static final String UPDATE_HEAD = BASE_URL_HTTP + "/user/avatar";
    //意见反馈 post
    public static final String FEEDBACK = BASE_URL_HTTP + "/sys/feedback/add";

    /*Request Codes*/
    public static final int REQUEST_CODE_PERMISSION = 1001;//动态权限申请的
    public static final int REQUEST_CODE_O_PERMISSION = 1002;//8.0安全授权申请的
    public static final int REQUEST_CODE_WEB_SELECT_FILE = 1003;//从WebView获取本机图片的
    public static final int REQUEST_CODE_WEB_SELECT_FILE_OFF_41 = 1004;//从WebView获取本机图片 4.1版本及以下
    public static final int REQUEST_CODE_SELECTED_IMAGES = 1005;//选择（单张/多张）图片
    public static final int REQUEST_CODE_SELECTED_IMAGES_EDIT = 1006;//已选图片列表的编辑
    public static final int REQUEST_CODE_OPEN_CAMERA = 1007;//打开相机
    public static final int REQUEST_CODE_IMAGE_CROP = 1008;//图片裁剪
    /*Error Codes*/
    public static final int ERROR_CODE_LOGOFF = 100001;//账号被挤下线
    public static final int ERROR_CODE_LOGIN_ERROR = 100401;//登录失效
}
