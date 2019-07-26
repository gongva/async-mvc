package com.vivi.asyncmvc.comm.view.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.studio.view.ViewUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.noober.background.drawable.DrawableCreator;

import java.util.ArrayList;
import java.util.List;

public class ActionSheet {

    private Context context;
    private TouchableDialog dialog;
    private TextView txtCancel;
    private LinearLayout contentLayout;
    private List<SheetItem> sheetItemList;
    private Display display;

    public ActionSheet(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        // 获取Dialog布局
        View view = createView(context);
        view.setMinimumWidth(display.getWidth()); // 设置Dialog最小宽度为屏幕宽度

        // 获取自定义Dialog布局中的控件
        contentLayout = view.findViewById(R.id.llt_action_sheet_content);
        txtCancel = view.findViewById(R.id.tv_action_sheet_cancel);
        txtCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 定义Dialog布局和参数
        dialog = new TouchableDialog(context, R.style.AlertDialogBottomStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
    }

    protected View createView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.dialog_action_sheet, null);
    }

    public static void show(Context context, String buttonText, ActionSheet.OnSheetItemClickListener listener) {
        ActionSheet.create(context)
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem(buttonText, SheetItemColor.Black, listener)
                .show();
    }

    public static ActionSheet create(Context context) {
        return new ActionSheet(context);
    }

    @Deprecated
    public ActionSheet builder() {
        return this;
    }

    public ActionSheet setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public ActionSheet setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public ActionSheet addSheetItem(String strItem, OnSheetItemClickListener listener) {
        return addSheetItem(strItem, SheetItemColor.Black, listener);
    }

    /**
     * @param strItem  条目名称
     * @param color    条目字体颜色，设置null则默认黑色
     * @param listener
     * @return
     */
    public ActionSheet addSheetItem(String strItem, SheetItemColor color, OnSheetItemClickListener listener) {
        if (sheetItemList == null) {
            sheetItemList = new ArrayList<>();
        }
        sheetItemList.add(new SheetItem(strItem, color, 16, listener));
        return this;
    }

    /**
     * @param strItem  条目名称
     * @param color    条目字体颜色，设置null则默认黑色
     * @param textSize 条目字体大小
     * @param listener
     * @return
     */
    public ActionSheet addSheetItem(String strItem, SheetItemColor color, int textSize, OnSheetItemClickListener listener) {
        if (sheetItemList == null) {
            sheetItemList = new ArrayList<>();
        }
        sheetItemList.add(new SheetItem(strItem, color, textSize, listener));
        return this;
    }

    /**
     * 设置条目布局
     */
    private void setSheetItems() {
        if (sheetItemList == null || sheetItemList.size() <= 0) {
            return;
        }

        int size = sheetItemList.size();

        // 高度控制，非最佳解决办法
        // 添加条目过多的时候控制高度
        if (size >= 7) {
            LayoutParams params = (LayoutParams) contentLayout.getLayoutParams();
            params.height = display.getHeight() / 2;
            contentLayout.setLayoutParams(params);
        }

        // 循环添加条目
        for (int i = 0; i < size; i++) {
            final int index = i;
            SheetItem sheetItem = sheetItemList.get(i);
            String strItem = sheetItem.name;
            SheetItemColor color = sheetItem.color;
            final OnSheetItemClickListener listener = sheetItem.itemClickListener;

            TextView textView = new TextView(context);
            textView.setText(strItem);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sheetItem.textSize);
            textView.setGravity(Gravity.CENTER);

            // 字体颜色
            if (color == null) {
                textView.setTextColor(Color.parseColor(SheetItemColor.Black.getName()));
            } else {
                textView.setTextColor(Color.parseColor(color.getName()));
            }

            // 高度
            float scale = context.getResources().getDisplayMetrics().density;
            int height = (int) (48 * scale + 0.5f);
            LayoutParams itemParam = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            textView.setLayoutParams(itemParam);

            // 点击事件
            if (listener != null) {
                textView.setClickable(true);
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onClick(index);
                    }
                });
            } else {
                textView.setClickable(false);
            }

            contentLayout.addView(textView);

            // 背景图片
            int dp10 = ViewUtils.dip2px(context, 10);
            if (size == 1) {
                textView.setBackground(getBtnDrawableCreator().setCornersRadius(dp10).build());
            } else {
                if (i == 0) {
                    textView.setBackground(getBtnDrawableCreator().setCornersRadius(0, 0, dp10, dp10).build());
                    contentLayout.addView(getLine());
                } else if (i == size - 1) {
                    textView.setBackground(getBtnDrawableCreator().setCornersRadius(dp10, dp10, 0, 0).build());
                } else {
                    textView.setBackground(getBtnDrawableCreator().build());
                    contentLayout.addView(getLine());
                }
            }
        }
    }

    private DrawableCreator.Builder getBtnDrawableCreator() {
        Resources res = context.getResources();
        return new DrawableCreator.Builder()
                .setPressedSolidColor(res.getColor(R.color.btn_white_press), res.getColor(R.color.white));
    }

    /**
     * 获取分割线
     *
     * @return
     */
    private View getLine() {
        View line = new View(context);
        line.setBackgroundColor(context.getResources().getColor(R.color.line));
        LayoutParams itemParam = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        itemParam.leftMargin = ViewUtils.dip2px(context, 15);
        itemParam.rightMargin = itemParam.leftMargin;
        line.setLayoutParams(itemParam);

        LinearLayout lltLine = new LinearLayout(context);
        lltLine.setBackgroundColor(context.getResources().getColor(R.color.white));
        lltLine.addView(line);
        return lltLine;
    }

    public void show() {
        setSheetItems();
        dialog.show();
    }

    public interface OnSheetItemClickListener {
        void onClick(int index);
    }

    public class SheetItem {
        String name;
        int textSize = 16;
        OnSheetItemClickListener itemClickListener;
        SheetItemColor color = SheetItemColor.Black;

        public SheetItem(String name, OnSheetItemClickListener listener) {
            this(name, SheetItemColor.Black, 16, listener);
        }

        public SheetItem(String name, SheetItemColor color, int textSize, OnSheetItemClickListener listener) {
            this.name = name;
            this.color = color;
            this.textSize = textSize;
            this.itemClickListener = listener;
        }
    }

    public enum SheetItemColor {
        Red("#FF6153"), Black("#242424"), Grey("#797979");

        private String name;

        SheetItemColor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
