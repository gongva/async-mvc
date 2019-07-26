package com.vivi.asyncmvc.ui.home.clicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.CLicenseRefreshEvent;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.UI;
import com.squareup.otto.Subscribe;

import butterknife.OnClick;

/**
 * 备案机动车-选择备案方式
 *
 * @author gongwei
 * @date 2019/2/2
 */
public class CLicenseBindModeActivity extends BaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_clicense_bind_mode;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, CLicenseBindModeActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.bindLifecycle(this);
        setTitle("选择备案方式");
    }

    @OnClick({R.id.llt_clicense_bind_scan, R.id.llt_clicense_bind_manual})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llt_clicense_bind_scan:
                UI.showToast("扫描");
                break;
            case R.id.llt_clicense_bind_manual:
                CLicenseBindActivity.start(this);
                break;
        }
    }

    @Subscribe
    public void onEvent(CLicenseRefreshEvent event) {
        finish();
    }
}
