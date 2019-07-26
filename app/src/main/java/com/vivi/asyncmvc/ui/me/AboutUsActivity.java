package com.vivi.asyncmvc.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.managers.upgrade.UpgradeManager;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.UI;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于
 *
 * @author gongwei
 * @date gongwei
 */
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.tv_about_version)
    TextView tvAboutVersion;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_about_us;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, AboutUsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("关于");

        tvAboutVersion.setText(String.format("电子驾照 v%s", AppConfig.getAppVersionName()));
    }

    @OnClick({R.id.tv_about_version_list, R.id.tv_about_upgrade})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_about_version_list:
                VersionLogActivity.start(this);
                break;
            case R.id.tv_about_upgrade:
                showLoadingDialog();
                UpgradeManager.getInstance().checkAppUpgradeStart(new JsonResultCallback<JsonResultVoid>() {
                    @Override
                    public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                        super.onFailure(statusCode, responseString, throwable, tag);
                        UI.showToast(responseString);
                        dismissLoadingDialog();
                    }
                });
                break;
        }
    }
}
