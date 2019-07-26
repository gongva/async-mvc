package com.vivi.asyncmvc.ui.home.homepage;

import android.app.Activity;
import android.os.Bundle;
import android.studio.view.ViewUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.AppModule;
import com.vivi.asyncmvc.api.entity.article.Article;
import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.api.entity.DriverLicense;
import com.vivi.asyncmvc.api.entity.HomeCard;
import com.vivi.asyncmvc.api.entity.Message;
import com.vivi.asyncmvc.base.BaseFragment;
import com.vivi.asyncmvc.comm.AppLifecycle;
import com.vivi.asyncmvc.comm.event.CLicenseRefreshEvent;
import com.vivi.asyncmvc.comm.event.DLicenseRefreshEvent;
import com.vivi.asyncmvc.comm.event.SaveMyModulesEvent;
import com.vivi.asyncmvc.comm.view.listview.ListLinearLayout;
import com.vivi.asyncmvc.comm.view.scroll.detectedscroll.DetectedScrollView;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultTsList;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.comm.web.WebActivity;
import com.vivi.asyncmvc.ui.home.ConsultActivity;
import com.vivi.asyncmvc.ui.home.article.ArticleCategoryActivity;
import com.vivi.asyncmvc.ui.home.clicense.CLicenseBindModeActivity;
import com.vivi.asyncmvc.ui.home.clicense.CLicenseQrCodeActivity;
import com.vivi.asyncmvc.ui.home.dlicense.DLicenseBindCheckActivity;
import com.vivi.asyncmvc.ui.home.dlicense.DLicenseQrCodeActivity;
import com.vivi.asyncmvc.ui.home.message.MessageAdapter;
import com.vivi.asyncmvc.ui.home.message.MessageListActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页Fragment
 */
public class HomeFragment extends BaseFragment {

    //final
    private final int COUNT_MESSAGE = 10;//首页显示消息的条数
    //views
    @BindView(R.id.llt_home_nav)
    LinearLayout lltHomeNav;
    @BindView(R.id.iv_home_nav_div_lic)
    ImageView ivMainNavDivLic;
    @BindView(R.id.iv_home_nav_car_lic)
    ImageView ivMainNavCarLic;
    @BindView(R.id.iv_home_message_float)
    ImageView ivHomeMessageFloat;
    @BindView(R.id.sv_home_root)
    DetectedScrollView svHomeRoot;
    @BindView(R.id.rfl_home)
    SmartRefreshLayout rflHome;
    @BindView(R.id.iv_home_safe_drive)
    ImageView ivHomeSafeDrive;
    @BindView(R.id.iv_home_service)
    ImageView ivHomeService;
    @BindView(R.id.iv_home_message)
    ImageView ivHomeMessage;
    @BindView(R.id.hcv_home_card)
    HomeCardView hcvHomeCard;
    @BindView(R.id.hav_home_article)
    HomeArticleView havHomeArticle;
    @BindView(R.id.bv_home_module)
    HomeModuleBannerView bvHomeModule;
    @BindView(R.id.llt_home_message)
    LinearLayout lltHomeMessage;
    @BindView(R.id.lv_home_message)
    ListLinearLayout lvHomeMessage;
    //tools
    //是否刷新完成：卡片、资讯、智慧服务
    private boolean isCardComplete, isArticleComplete, isMessageComplete;
    private MessageAdapter mMessageAdapter;
    //data
    private DriverLicense mDriverLicense;
    private List<CarLicense> mCarLicenseList;

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        BusProvider.bindLifecycle(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDataLocal();
        refreshPage();
    }

    private void initView() {
        hideActionBar();
        //关闭上拉翻页
        rflHome.finishLoadMoreWithNoMoreData();
        rflHome.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshPage();
            }
        });
        //设置渐变标题栏
        svHomeRoot.setViewFaded(lltHomeNav);
        //卡片
        /*ArrayList<CarLicense> licensesAll = new ArrayList<>();
        CarLicense car1 = new CarLicense();
        car1.id = "1";
        car1.plateNum = "贵A111111";
        car1.ownerName = "熊伟民";
        car1.unhandleCount = 3;
        car1.isOwner = false;
        CarLicense car2 = new CarLicense();
        car2.id = "2";
        car2.plateNum = "贵A222222";
        car2.ownerName = "熊伟民";
        car2.unhandleCount = 3;
        car2.isOwner = false;
        CarLicense car3 = new CarLicense();
        car3.id = "3";
        car3.plateNum = "贵A333333";
        car3.ownerName = "熊伟民";
        car3.unhandleCount = 3;
        car3.isOwner = false;

        CarLicense car4 = new CarLicense();
        car4.id = "4";
        car4.plateNum = "贵A444444";
        car4.ownerName = "龚伟";
        car4.unhandleCount = 0;
        car4.isOwner = true;
        CarLicense car5 = new CarLicense();
        car5.id = "5";
        car5.plateNum = "贵A555555";
        car5.ownerName = "龚伟";
        car5.unhandleCount = 0;
        car5.isOwner = true;
        CarLicense car6 = new CarLicense();
        car6.id = "6";
        car6.plateNum = "贵A666666";
        car6.ownerName = "龚伟";
        car6.unhandleCount = 0;
        car6.isOwner = true;
        CarLicense car7 = new CarLicense();
        car7.id = "7";
        car7.plateNum = "贵A777777";
        car7.ownerName = "龚伟";
        car7.unhandleCount = 0;
        car7.isOwner = true;
        licensesAll.add(car1);
        licensesAll.add(car2);
        licensesAll.add(car3);
        licensesAll.add(car4);
        licensesAll.add(car5);
        licensesAll.add(car6);
        licensesAll.add(car7);
        CarLicense.save(licensesAll);*/
        LinearLayout.LayoutParams cardLayoutParams = (LinearLayout.LayoutParams) hcvHomeCard.getLayoutParams();
        cardLayoutParams.height = OS.getHeightByRatioAndScreenWidth(getActivity(), 16f / 12);
        hcvHomeCard.setLayoutParams(cardLayoutParams);
        //功能模块
        bvHomeModule.setHomeModule(new HomeModuleBannerAdapter.HomeModuleCallBack() {
            @Override
            public void onClick(AppModule appModule) {
                AppModule.startToModuleActivity(getActivity(), appModule);
            }
        });
        //资讯
        ArrayList<Article> tempArticle = new ArrayList<>();
        Article a1 = new Article();
        a1.title = "我是1";
        Article a2 = new Article();
        a2.title = "我是222222222";
        Article a3 = new Article();
        a3.title = "我是333333333333333";
        tempArticle.add(a1);
        tempArticle.add(a2);
        tempArticle.add(a3);
        tempArticle.add(new Article());
        havHomeArticle.setData(tempArticle, new HomeArticleView.HomeArticleCallBack() {
            @Override
            public void clickArticle(Article article) {
                UI.showToast(article.title);
            }
        });
        AppLifecycle.setLifecycle(this, new AppLifecycle.LifecycleCallback() {
            @Override
            public void onActivityResumed(Activity activity) {
                super.onActivityResumed(activity);
                havHomeArticle.startAutoCycle();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                super.onActivityPaused(activity);
                havHomeArticle.stopAutoCycle();
            }
        });
        //设置安全行驶和温馨服务16:8.5
        set32to17LayoutParams(ivHomeSafeDrive);
        set32to17LayoutParams(ivHomeService);
        //智慧服务
        lltHomeMessage.setVisibility(View.GONE);
        mMessageAdapter = new MessageAdapter(getActivity());
        mMessageAdapter.setOnItemLongClickListener(new MessageAdapter.MessageOnItemLongClickListener() {
            @Override
            public void onItemLongClick(final Message message) {
                UI.showConfirmDialog(getActivity(), "您要从首页移除这条消息吗？", "取消", null, "移除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingDialog();
                        Api.messageHomeNotShow(message.id, new JsonResultCallback<JsonResultVoid>() {
                            @Override
                            public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                                dismissLoadingDialog();
                                mMessageAdapter.getData().remove(message);
                                mMessageAdapter.notifyDataSetChanged();
                                Message.updateAsHomeNotShow(message.id);
                            }

                            @Override
                            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                                super.onFailure(statusCode, responseString, throwable, tag);
                                dismissLoadingDialog();
                                UI.showToast(responseString);
                            }
                        });
                    }
                });
            }
        });
        lvHomeMessage.setAdapter(mMessageAdapter);
    }

    /**
     * 加载DB数据至页面
     */
    private void setDataLocal() {
        //card
        setHomeCardData(DriverLicense.query(), CarLicense.queryAll());
        //message
        List<Message> messages = Message.queryForHome(COUNT_MESSAGE);
        setMessage(messages);
    }

    /**
     * 刷新整个页面
     */
    private void refreshPage() {
        refreshHomeCardData();
        refreshMessage();
    }

    /**
     * 检查所有接口是否都已返回结果
     */
    private void checkRefreshComplete() {
        if (isAllComplete()) {
            dismissLoadingDialog();
            rflHome.finishRefresh();
            rflHome.finishLoadMoreWithNoMoreData();
        }
    }

    /**
     * 是否刷新完成
     *
     * @return
     */
    private boolean isAllComplete() {
        return isCardComplete && isMessageComplete/* && isArticleComplete */;
    }

    /**
     * 保存驾驶证/行驶证数据，并更新页面
     *
     * @param driverLicense
     * @param carLicense
     */
    private void setHomeCardData(DriverLicense driverLicense, List<CarLicense> carLicense) {
        mDriverLicense = driverLicense;
        mCarLicenseList = carLicense;
        //car license icon visible?
        ivMainNavCarLic.setVisibility(mDriverLicense == null ? View.GONE : View.VISIBLE);
        //card view
        hcvHomeCard.setLicense(driverLicense, carLicense, new HomeLicenseBannerAdapter.HomeLicenseBannerCallBack() {
            @Override
            public void clickDriverLicenseBind() {
                DLicenseBindCheckActivity.start(getActivity());
            }

            @Override
            public void clickCarLicenseBind() {
                CLicenseBindModeActivity.start(getActivity());
            }

            @Override
            public void clickScoreLast() {
                WebActivity.start(getActivity(), "http://www.baidu.com", "我是百度", true);
            }

            @Override
            public void clickQRCodeDriverLicense(DriverLicense obj) {
                DLicenseQrCodeActivity.start(getActivity(), obj);
            }

            @Override
            public void clickQRCodeCarLicense(CarLicense obj) {
                CLicenseQrCodeActivity.start(getActivity(), obj);
            }
        });
    }

    /**
     * 刷新驾驶证/行驶证From server
     */
    private void refreshHomeCardData() {
        isCardComplete = false;
        Api.getHomeCard(new JsonResultCallback<JsonResult<HomeCard>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<HomeCard> response, int tag) {
                HomeCard homeCard = response.getData();
                if (homeCard != null) {
                    //save data
                    DriverLicense.save(homeCard.driverLicense);
                    CarLicense.save(homeCard.vehicleLicenses);

                    setHomeCardData(homeCard.driverLicense, homeCard.vehicleLicenses);
                }
                isCardComplete = true;
                checkRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                UI.showToast(responseString);
                isCardComplete = true;
                checkRefreshComplete();
            }
        });
    }

    /**
     * 设置安全行驶和温馨服务ImageView宽高比为16:8.5
     *
     * @param imageView
     */
    private void set32to17LayoutParams(ImageView imageView) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        int width = (OS.getScreenWidth(getActivity()) - ViewUtils.dip2px(getActivity(), 40)) / 2;
        params.height = OS.getHeightByRatioAndWidth(32f / 17, width);
        imageView.setLayoutParams(params);
    }

    /**
     * 显示智慧服务列表
     *
     * @param messages
     */
    private void setMessage(List<Message> messages) {
        mMessageAdapter.clear();
        mMessageAdapter.addAll(messages);
        lltHomeMessage.setVisibility(mMessageAdapter.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /**
     * 刷新智慧服务数据
     */
    private void refreshMessage() {
        isMessageComplete = false;
        Api.getMessageList(true, 0, new JsonResultCallback<JsonResultTsList<Message>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultTsList<Message> response, int tag) {
                List<Message> listResult = response.getData();
                if (listResult != null) {
                    setMessage(listResult);
                    Message.save(listResult);
                }
                isMessageComplete = true;
                checkRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                UI.showToast(responseString);
                isMessageComplete = true;
                checkRefreshComplete();
            }
        });
    }

    @OnClick({R.id.iv_home_safe_drive, R.id.iv_home_service, R.id.iv_home_message, R.id.iv_home_nav_div_lic, R.id.iv_home_nav_car_lic, R.id.iv_home_message_float})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_home_safe_drive:
                ArticleCategoryActivity.start(getActivity());
                break;
            case R.id.iv_home_service:
                ConsultActivity.start(getActivity());
                break;
            case R.id.iv_home_message:
                MessageListActivity.start(getActivity());
                break;
            case R.id.iv_home_nav_div_lic:
                //test driver license
                /*DriverLicense temp = new DriverLicense();
                temp.realName = "张三";
                temp.licenseType = "C12";
                temp.expiredDate = System.currentTimeMillis();
                temp.qrInfo = "522321********4029,王*倩,女,D,0,156,贵州省兴义市顶效镇********组51号,1995-08-03 00:00:00,2015-08-05 00:00:00,2015-08-05 00:00:00,2021-08-05 00:00:00,5201******56,贵E,6,,,,";
                DLicenseQrCodeActivity.start(getActivity(), temp);*/

                //test car license
                /*CarLicense temp = new CarLicense();
                temp.id = "123";// 行驶证ID
                temp.plateNum = "贵A123456";// 车牌号
                temp.operationType = "A";// 汽车使用性质:A
                temp.operationTypeName = "营运车";//汽车使用性质名称:营运车辆
                temp.vehicleType = "02";// 车辆类型:02
                temp.vehicleTypeName = "巨型车辆";//车辆类型名称:小型车辆
                temp.issueDate = System.currentTimeMillis();// 发证日期
                temp.ownerName = "龚伟";// 所有人名字
                temp.isOwner = true;// 是否本人机动车
                temp.phone = "13212345678";
                temp.unhandleCount = 3;//未处理违法数
                temp.expiredDate = System.currentTimeMillis();//有效期止
                temp.qrInfo = "522321********4029,王*倩,女,D,0,156,贵州省兴义市顶效镇********组51号,1995-08-03 00:00:00,2015-08-05 00:00:00,2015-08-05 00:00:00,2021-08-05 00:00:00,5201******56,贵E,6,,,,";
                CLicenseQrCodeActivity.start(getActivity(), temp);*/

                if (mDriverLicense == null) {
                    DLicenseBindCheckActivity.start(getActivity());
                } else {
                    DLicenseQrCodeActivity.start(getActivity(), mDriverLicense);
                }
                break;
            case R.id.iv_home_nav_car_lic:
                if (mCarLicenseList == null || mCarLicenseList.size() == 0) {
                    CLicenseBindModeActivity.start(getActivity());
                } else {
                    CLicenseQrCodeActivity.start(getActivity(), mCarLicenseList.get(0));
                }
                break;
            case R.id.iv_home_message_float:
                MessageListActivity.start(getActivity());
                break;
        }
    }

    @Subscribe
    public void onEvent(SaveMyModulesEvent event) {
        bvHomeModule.refresh();
    }

    @Subscribe
    public void onEvent(DLicenseRefreshEvent event) {
        refreshHomeCardData();
    }

    @Subscribe
    public void onEvent(CLicenseRefreshEvent event) {
        refreshHomeCardData();
    }
}
