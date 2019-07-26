package com.vivi.asyncmvc.ui.comm.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.ActivitySupport;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.location.SelectMapLocationEvent;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.squareup.otto.Subscribe;

import butterknife.BindView;

public class MapActivity extends BaseActivity {

    @BindView(R.id.mv_map)
    MapView mvMap;

    private BaiduMap mBaiduMap;

    //百度定位LocationClient
    public LocationClient mLocationClient = null;
    //百度定位Receiver
    public HikLocationListener mLocationListener = new HikLocationListener(SelectMapLocationEvent.class);

    public static void start(final Activity context) {
        ActivitySupport.requestRunPermission(context, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionRequestListener() {
            @Override
            public void onGranted() {
                context.startActivity(new Intent(context, MapActivity.class));
            }
        });
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_map;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.bindLifecycle(this);
        setTitle("地图");
        initMap();
        startLocation();
    }

    private void initMap() {
        mBaiduMap = mvMap.getMap();
        //关闭缩放按钮
        mvMap.showZoomControls(false);
        //开启实时路况
        mBaiduMap.setTrafficEnabled(true);

        //设置中心点
        //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(30, 104)));


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

    /**
     * 开始定位
     */
    private void startLocation() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(mLocationListener);
        mLocationClient.setLocOption(HikLocationListener.getLocationOption());
        mLocationClient.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mvMap != null) {
            mvMap.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (mvMap != null) {
            mvMap.onDestroy();
            mvMap = null;
        }
        cleanLocationClient();
    }

    private void cleanLocationClient() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient = null;
        }
    }

    @Subscribe
    public void onEvent(SelectMapLocationEvent event) {
        mLocationClient.unRegisterLocationListener(mLocationListener);
        cleanLocationClient();
        //设置中心点，缩放级别12
        LatLng latLng = new LatLng(event.bdLocation.getLatitude(), event.bdLocation.getLongitude());
        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(16).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }
}
