package com.vivi.asyncmvc.ui.home.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.studio.util.DateUtils;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;

import java.util.Date;

import butterknife.BindView;

/**
 * 消息中心-详情
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class MessageDetailActivity extends BaseActivity {

    @BindView(R.id.tv_message_detail_title)
    TextView tvTitle;
    @BindView(R.id.tv_message_detail_datetime)
    TextView tvDatetime;
    @BindView(R.id.tv_message_detail_description)
    TextView tvDescription;
    //data
    private String title, description;
    private long datetime;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message_detail;
    }

    public static void start(Context context, String title, long datetime, String description) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("datetime", datetime);
        intent.putExtra("description", description);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        title = intent.getStringExtra("title");
        datetime = intent.getLongExtra("datetime", System.currentTimeMillis());
        description = intent.getStringExtra("description");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("消息详情");
        tvTitle.setText(title);
        tvDatetime.setText(DateUtils.formatDateTime(new Date(datetime)));
        tvDescription.setText(description);
    }
}
