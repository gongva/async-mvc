package com.vivi.asyncmvc.ui.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.ui.comm.selectimg.BrowseImgsActivity;
import com.vivi.asyncmvc.ui.comm.selectimg.MultiImageSelectorActivity;
import com.vivi.asyncmvc.library.utils.UI;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class SActivity extends BaseActivity {

    @BindView(R.id.mapview)
    MapView mapview;

    private BaiduMap mBaiduMap;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_s;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //super.setStatusBarTransparent();
        //super.setStatusIconDark(false);
        super.onCreate(savedInstanceState);
        //hideActionBar();

        //ActionSheet弹框
        /*ActionSheet.create(getActivity())
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("退出当前账号后不会删除任何历史数据", ActionSheet.SheetItemColor.Grey, 12, null)
                .addSheetItem("退出登录", ActionSheet.SheetItemColor.Red, new ActionSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        UI.showToast("退出登录了");
                    }
                }).show();*/

        //跳转网页
        /*WebActivity.start(getActivity(), "http://www.baidu.com", "我是百度", true);*/

        //加载地图
        initMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    private void initMap() {
        //关闭缩放按钮
        mapview.showZoomControls(false);

        mBaiduMap = mapview.getMap();

        //开启实时路况
        mBaiduMap.setTrafficEnabled(true);

        //设置中心点
        //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(30, 104)));

        //设置默认的缩放级别
        //mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(12));

        //marker点击事件
        /*BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker){
                return false;
            }
        };
        mBaiduMap.setOnMarkerClickListener(markerClickListener);*/


        //poi检索
        /*OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){

            @Override
            public void onGetPoiResult(PoiResult result){
                //获取POI检索结果
            }
            @Override
            public void onGetPoiDetailResult(PoiDetailResult result){
                //获取Place详情页检索结果
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                //室内检索结果?
            }
        };
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        poiSearch.searchInCity((new PoiCitySearchOption())
                .city("贵阳")
                .keyword("医院")
                .pageNum(10));
        poiSearch.destroy();*/


        //在线建议查询
        /*OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
            public void onGetSuggestionResult(SuggestionResult res) {

                if (res == null || res.getAllSuggestions() == null) {
                    return;
                    //未找到相关结果
                }

                //获取在线建议检索结果
            }
        };
        SuggestionSearch mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);
        // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword("百度")
                .city("北京"));
        mSuggestionSearch.destroy();*/
    }

    /*private void initRefreshLayout() {
        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000);
            }
        });
    }*/

    ArrayList<String> temp;

    @OnClick({R.id.xuantu, R.id.bianjitu, R.id.chakantu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.xuantu:
                MultiImageSelectorActivity.startForMulti(this, 9, null);
                break;
            case R.id.bianjitu:
                BrowseImgsActivity.startForSelected(this, temp, 0);
                break;
            case R.id.chakantu:
                //BrowseImgsActivity.startForShowImg(this, temp, 0);
                showLoadingDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppContext.REQUEST_CODE_SELECTED_IMAGES:
                    temp = MultiImageSelectorActivity.getImagesFromResultOk(data);
                    UI.showConfirmDialog(this, "选图回来了：" + temp, "知道了", null);
                    break;
                case AppContext.REQUEST_CODE_SELECTED_IMAGES_EDIT:
                    temp = BrowseImgsActivity.getImagesFromResultOk(data);
                    UI.showConfirmDialog(this, "编辑回来了：" + temp, "知道了", null);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapview != null) {
            mapview.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapview != null) {
            mapview.onDestroy();
        }
    }
}
