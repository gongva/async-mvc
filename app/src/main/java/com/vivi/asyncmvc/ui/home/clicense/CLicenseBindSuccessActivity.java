package com.vivi.asyncmvc.ui.home.clicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;

import butterknife.OnClick;

/**
 * 备案机动车成功页面
 *
 * @author gongwei
 * @date 2019/2/2
 */
public class CLicenseBindSuccessActivity extends BaseActivity {
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_clicense_bind_success;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CLicenseBindSuccessActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("备案结果");
    }

    @OnClick(R.id.btn_clicense_bind_success)
    public void onClick() {
        finish();
    }
}
