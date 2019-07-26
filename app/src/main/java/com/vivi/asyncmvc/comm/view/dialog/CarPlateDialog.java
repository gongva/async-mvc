package com.vivi.asyncmvc.comm.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.studio.view.ViewUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.view.SquareLayout;
import com.noober.background.drawable.DrawableCreator;

import java.util.Arrays;
import java.util.List;

/**
 * 车牌号前缀选择器
 *
 * @author gongwei
 * @date 2019/2/2
 */
public class CarPlateDialog {
    //tools
    private Context mContext;
    private CarPlateDialogCallback mCallBack;
    private final int SINGLE_ROW_MAX_COL = 7;//一排放7个
    //views
    private TouchableDialog dialog;
    private LinearLayout lltContent;
    private TextView tvCancel;

    //选项集合
    private String[] provinces = {"京", "沪", "浙", "苏", "粤", "鲁", "晋", "冀", "豫", "川", "渝", "辽", "吉", "黑",
            "皖", "鄂", "湘", "赣", "闽", "陕", "甘", "宁", "蒙", "津", "贵", "云", "桂", "琼", "青", "新", "藏"};
    //可选集合
    private String[] provincesEnable = {"贵"};

    public static CarPlateDialog newInstance(Activity activity, CarPlateDialogCallback callback) {
        return new CarPlateDialog(activity, callback);
    }

    public CarPlateDialog(Activity activity, CarPlateDialogCallback callback) {
        this.mContext = activity;
        this.mCallBack = callback;
        //view
        View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_car_plate, null);
        lltContent = rootView.findViewById(R.id.llt_dialog_province_content);
        tvCancel = rootView.findViewById(R.id.tv_dialog_province_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //data
        List<String> provincesEnableList = Arrays.asList(provincesEnable);
        int dp5 = ViewUtils.dip2px(mContext, 5);
        int dp4 = ViewUtils.dip2px(mContext, 4);
        Resources res = mContext.getResources();
        Drawable drawableEnable = new DrawableCreator.Builder().setPressedSolidColor(res.getColor(R.color.btn_4E91F9_press), res.getColor(R.color.btn_4E91F9)).setCornersRadius(dp4).build();
        Drawable drawableUnEnable = new DrawableCreator.Builder().setSolidColor(res.getColor(R.color.btn_disable)).setCornersRadius(dp4).build();

        LinearLayout lltRow = null;
        LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParam.weight = 1;
        for (int i = 0; i < provinces.length; i++) {
            //new line
            if (i % SINGLE_ROW_MAX_COL == 0) {
                lltRow = new LinearLayout(mContext);
                lltRow.setWeightSum(SINGLE_ROW_MAX_COL);
                lltContent.addView(lltRow);
            }
            //new province
            final String strProvince = provinces[i];

            TextView tvItem = newItemTextView(res, strProvince);
            SquareLayout squareLayout = new SquareLayout(mContext);
            squareLayout.setPadding(dp5, dp5, dp5, dp5);
            squareLayout.addView(tvItem, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            lltRow.addView(squareLayout, itemParam);

            if (provincesEnableList.contains(strProvince)) {
                //usable
                tvItem.setBackground(drawableEnable);
                tvItem.setClickable(true);
                tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.choose(strProvince);
                        }
                        dismiss();
                    }
                });
            } else {
                //unusable
                tvItem.setBackground(drawableUnEnable);
                tvItem.setClickable(false);
            }
        }
        //dialog
        dialog = new TouchableDialog(activity, R.style.AlertDialogBottomStyle);
        dialog.setContentView(rootView);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
    }

    private TextView newItemTextView(Resources res, String strProvince) {
        TextView tvItem = new TextView(mContext);
        tvItem.setText(strProvince);
        tvItem.setGravity(Gravity.CENTER);
        tvItem.setTextSize(16);
        tvItem.setTextColor(res.getColor(R.color.white));
        return tvItem;
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface CarPlateDialogCallback {
        void choose(String province);
    }
}