package com.vivi.asyncmvc.comm.view.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.library.utils.OS;

import java.util.List;

/**
 * 列表选择器
 *
 * @author gongwei
 * @date 2019/2/12
 */
public class ListDialog {
    //tools
    private Activity mContext;
    private ListDialogCallback mCallBack;
    //views
    private TouchableDialog dialog;
    private TextView tvTitle;
    private ListView lvContent;
    //data
    private CharSequence mTitle;
    private List<String> mData;

    public static ListDialog newInstance(Activity activity, CharSequence title, List<String> data, ListDialogCallback callback) {
        return new ListDialog(activity, title, data, callback);
    }

    public ListDialog(Activity activity, CharSequence title, List<String> data, ListDialogCallback callback) {
        this.mContext = activity;
        this.mCallBack = callback;
        this.mTitle = title;
        this.mData = data;
        //view
        View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_list, null);
        tvTitle = rootView.findViewById(R.id.tv_list_dialog_title);
        lvContent = rootView.findViewById(R.id.lv_list_dialog_content);
        //data
        tvTitle.setText(mTitle);
        if (mData != null) {
            ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, mData);
            lvContent.setAdapter(adapter);
            lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    if (mCallBack != null) {
                        mCallBack.choose(position);
                    }
                }
            });
        }
        //dialog
        dialog = new TouchableDialog(activity, R.style.AlertDialogStyle);
        dialog.setContentView(rootView);
        Window dialogWindow = dialog.getWindow();
        //dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (OS.getScreenWidth(mContext) * 0.9);
        if (mData != null & mData.size() > 10) {//数据超过10个，则固定高度
            lp.height = (int) (OS.getScreenHeight(mContext) * 0.7);
        }
        dialogWindow.setAttributes(lp);
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

    public interface ListDialogCallback {
        void choose(int which);
    }
}
