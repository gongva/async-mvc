package com.vivi.asyncmvc.ui.home.dlicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 绑定驾驶证检查结果，eg：名下无驾驶证
 *
 * @author gongwei
 * @date 2019/2/12
 */
public class DLicenseBindFailActivity extends BaseActivity {

    @BindView(R.id.tv_dlicense_bind_fail_notice)
    TextView tvNotice;
    //data
    private String notice;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_dlicense_bind_fail;
    }

    public static void start(Context context, String notice) {
        Intent intent = new Intent(context, DLicenseBindFailActivity.class);
        intent.putExtra("notice", notice);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        notice = intent.getStringExtra("notice");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("识别结果");
        tvNotice.setText(notice);
    }

    @OnClick(R.id.btn_dlicense_bind_fail)
    public void onClick() {
        finish();
    }
}
