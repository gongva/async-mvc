package com.vivi.asyncmvc.comm.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.Area;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultRow;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;

import java.util.ArrayList;
import java.util.List;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * 省市区区域选择器，独立完成：接口获取省市区、有/无默认值、完成后的数据回调(有可能为空)
 * 调用static show()方法启用
 *
 * @author gongwei
 * @date 22019.1.25
 */
public class AreaPickerDialog implements View.OnClickListener {

    //static
    private static final int MSG_SHOW_LOADING = 1;
    private static final int MSG_SET_PROVINCE = 2;
    private static final int MSG_SET_CITY = 3;
    private static final int MSG_SET_COUNTY = 4;
    //views
    private TouchableDialog mDialog;
    private static AreaPickerDialog INSTANTS;
    private NumberPickerView pickerProvince, pickerCity, pickerCounty;
    private ProgressBar pbAreaPicker;
    //data
    private static List<Area> provinceList = new ArrayList(), cityList = new ArrayList(), countyList = new ArrayList();//备选列表
    private static Area provinceDefault, cityDefault, countyDefault;//初始化是的默认值
    public String provinceOld, cityOld;//当前的省市，若滑动之后的省市没变，则不刷新了
    //tools
    private Context mContext;
    private AreaPickerCallBack mCallBack;
    private NumberPickerView.OnScrollListener mScrollListener = new NumberPickerView.OnScrollListener() {
        @Override
        public void onScrollStateChange(NumberPickerView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                switch (view.getId()) {
                    case R.id.pv_area_province:
                        String pTemp = view.getDisplayedValues()[view.getValue()];
                        if (!pTemp.equals(provinceOld)) {
                            //clear city and county first
                            cityList.clear();
                            countyList.clear();
                            setNumberPickerCity();
                            setNumberPickerCounty();
                            provinceOld = pTemp;
                            //get city then
                            getCity(getAreaByStr(provinceList, pTemp), null);
                        }
                        break;
                    case R.id.pv_area_city:
                        String cTemp = view.getDisplayedValues()[view.getValue()];
                        if (!cTemp.equals(cityOld)) {
                            //clear county first
                            countyList.clear();
                            setNumberPickerCounty();
                            cityOld = cTemp;
                            //get county then
                            getCounty(getAreaByStr(cityList, cTemp));
                        }
                        break;
                }
            }
        }
    };
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_LOADING:
                    if (INSTANTS != null) {
                        INSTANTS.showLoading((Boolean) msg.obj);
                    }
                    break;
                case MSG_SET_PROVINCE:
                    if (INSTANTS != null) {
                        INSTANTS.setNumberPickerProvince();
                    }
                    break;
                case MSG_SET_CITY:
                    if (INSTANTS != null) {
                        INSTANTS.setNumberPickerCity();
                    }
                    break;
                case MSG_SET_COUNTY:
                    if (INSTANTS != null) {
                        INSTANTS.setNumberPickerCounty();
                    }
                    break;
            }
        }
    };

    private AreaPickerDialog(@NonNull Context context, AreaPickerCallBack callBack) {
        this.mContext = context;
        this.mCallBack = callBack;

        //views
        View rootView = View.inflate(context, R.layout.dialog_area_picker, null);
        rootView.setMinimumWidth(OS.getScreenWidth(context));
        rootView.findViewById(R.id.tv_area_picker_cancel).setOnClickListener(this);
        rootView.findViewById(R.id.tv_area_picker_ok).setOnClickListener(this);
        pickerProvince = rootView.findViewById(R.id.pv_area_province);
        pickerCity = rootView.findViewById(R.id.pv_area_city);
        pickerCounty = rootView.findViewById(R.id.pv_area_county);
        pbAreaPicker = rootView.findViewById(R.id.pb_area_picker);
        mDialog = new TouchableDialog(context, R.style.AlertDialogBottomStyle);
        mDialog.setContentView(rootView);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        //align bottom
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
    }

    public static void show(final Context context, final Area provinceDefault, final Area cityArea, Area countyArea, final AreaPickerCallBack callBack) {
        AreaPickerDialog.provinceDefault = provinceDefault;
        AreaPickerDialog.cityDefault = cityArea;
        AreaPickerDialog.countyDefault = countyArea;
        INSTANTS = new AreaPickerDialog(context, callBack);
        if (provinceDefault != null) {
            INSTANTS.provinceOld = provinceDefault.name;
        }
        if (cityArea != null) {
            INSTANTS.cityOld = cityDefault.name;
        }
        INSTANTS.show();
        getProvince(provinceDefault, cityArea);
    }

    /**
     * 获取省列表
     *
     * @param provinceDefault
     * @param cityArea
     */
    private static void getProvince(final Area provinceDefault, final Area cityArea) {
        showLoadingByHandler(true);
        Api.getArea(null, new JsonResultCallback<JsonResultRow<Area>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultRow<Area> response, int tag) {
                provinceList = response.getData();
                if (!provinceList.isEmpty()) {
                    if (AreaPickerDialog.provinceDefault == null) {
                        AreaPickerDialog.provinceDefault = provinceList.get(0);
                    }
                    //to handler province
                    setNumberPickerProvinceByHandler();
                    //get city then
                    getCity(provinceDefault == null ? provinceList.get(0) : provinceDefault, cityArea);
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                showLoadingByHandler(false);
                UI.showToast(responseString);
            }
        });
    }

    /**
     * 根据省Code获取市列表
     *
     * @param provinceDefault
     * @param cityDefault
     */
    private static void getCity(Area provinceDefault, final Area cityDefault) {
        showLoadingByHandler(true);
        Api.getArea(provinceDefault.code, new JsonResultCallback<JsonResultRow<Area>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultRow<Area> response, int tag) {
                cityList = response.getData();
                if (!cityList.isEmpty()) {
                    if (AreaPickerDialog.cityDefault == null) {
                        AreaPickerDialog.cityDefault = cityList.get(0);
                    }
                    //to handler city
                    setNumberPickerCityByHandler();
                    //get county then
                    getCounty(cityDefault == null ? cityList.get(0) : cityDefault);
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                showLoadingByHandler(false);
                UI.showToast(responseString);
            }
        });
    }

    /**
     * 根据市Code获取区县列表
     *
     * @param cityDefault
     */
    private static void getCounty(Area cityDefault) {
        showLoadingByHandler(true);
        Api.getArea(cityDefault.code, new JsonResultCallback<JsonResultRow<Area>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultRow<Area> response, int tag) {
                countyList = response.getData();
                if (!countyList.isEmpty()) {
                    if (AreaPickerDialog.countyDefault == null) {
                        AreaPickerDialog.countyDefault = countyList.get(0);
                    }
                    //to handler county
                    setNumberPickerCountyByHandler();
                }
                showLoadingByHandler(false);
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                showLoadingByHandler(false);
                UI.showToast(responseString);
            }
        });
    }

    /**
     * 因为发起请求api的线程和显示dialog的线程并非同一个线程
     *
     * @param show
     */
    private static void showLoadingByHandler(boolean show) {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SHOW_LOADING;
        msg.obj = show;
        mHandler.sendMessage(msg);
    }

    private void showLoading(boolean show) {
        pbAreaPicker.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private static void setNumberPickerProvinceByHandler() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SET_PROVINCE;
        mHandler.sendMessage(msg);
    }

    private void setNumberPickerProvince() {
        String[] provinceArray = getProvinceArray();
        setNumberPicker(pickerProvince, provinceArray, calcIndex(provinceArray, provinceDefault));
    }

    private static void setNumberPickerCityByHandler() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SET_CITY;
        mHandler.sendMessage(msg);
    }

    private void setNumberPickerCity() {
        String[] cityArray = getCityArray();
        setNumberPicker(pickerCity, cityArray, calcIndex(cityArray, cityDefault));
    }

    private static void setNumberPickerCountyByHandler() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SET_COUNTY;
        mHandler.sendMessage(msg);
    }

    private void setNumberPickerCounty() {
        String[] countyArray = getCountyArray();
        setNumberPicker(pickerCounty, countyArray, calcIndex(countyArray, countyDefault));
    }

    private void setNumberPicker(NumberPickerView picker, String[] values, int mValue) {
        if (picker != null) {
            picker.setMinValue(0);
            /**
             * 1、当前NumberPicker的最大值大于数组大小时，需先setMaxValue再setDisplayedValues
             * 2、当前NumberPicker的最大值小于数组大小时，需先setDisplayedValues再setMaxValue
             */
            int oldMax = picker.getMaxValue();
            int newMax = values.length - 1;
            if (newMax > oldMax) {
                picker.setDisplayedValues(values);
                picker.setMaxValue(newMax);
            } else {
                picker.setMaxValue(newMax);
                picker.setDisplayedValues(values);
            }
            picker.setValue(mValue);
            picker.setOnScrollListener(mScrollListener);
        }
    }

    /**
     * List转数组：省
     *
     * @return
     */
    private String[] getProvinceArray() {
        String[] result = new String[provinceList.size()];
        for (int i = 0; i < provinceList.size(); i++) {
            result[i] = provinceList.get(i).name;
        }
        if (result.length == 0) {
            result = new String[]{" "};
        }
        return result;
    }

    /**
     * List转数组：市
     *
     * @return
     */
    private String[] getCityArray() {
        String[] result = new String[cityList.size()];
        for (int i = 0; i < cityList.size(); i++) {
            result[i] = cityList.get(i).name;
        }
        if (result.length == 0) {
            result = new String[]{" "};
        }
        return result;
    }

    /**
     * List转数组：区
     *
     * @return
     */
    private String[] getCountyArray() {
        String[] result = new String[countyList.size()];
        for (int i = 0; i < countyList.size(); i++) {
            result[i] = countyList.get(i).name;
        }
        if (result.length == 0) {
            result = new String[]{" "};
        }
        return result;
    }

    /**
     * 计算str在数组中的下标，用于计算picker默认的value
     *
     * @param array
     * @param area
     * @return
     */
    private int calcIndex(String[] array, Area area) {
        if (array == null || area == null) {
            return 0;
        }
        int length = array.length;
        for (int i = 0; i < length; i++) {
            if (array[i].equals(area.name)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 通过文字拿Code
     *
     * @return
     */
    private Area getAreaByStr(List<Area> list, String str) {
        for (Area area : list) {
            if (area.name.equals(str)) {
                return area;
            }
        }
        return null;
    }

    private void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        mDialog.dismiss();
        switch (v.getId()) {
            case R.id.tv_area_picker_cancel:
                mDialog.dismiss();
                break;
            case R.id.tv_area_picker_ok:
                if (mCallBack != null) {
                    Area province = null, city = null, county = null;
                    if (!provinceList.isEmpty()) {
                        province = provinceList.get(pickerProvince.getValue());
                    }
                    if (!cityList.isEmpty()) {
                        city = cityList.get(pickerCity.getValue());
                    }
                    if (!countyList.isEmpty()) {
                        county = countyList.get(pickerCounty.getValue());
                    }
                    mCallBack.done(province, city, county);
                }
                mDialog.dismiss();
                break;
        }
    }

    public interface AreaPickerCallBack {
        void done(Area province, Area city, Area county);
    }
}