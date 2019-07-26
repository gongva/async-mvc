package com.vivi.asyncmvc.comm.view.edit;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.AppConfig;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 带获取验证码的模拟EditText
 *
 * @author gongwei
 * @date 2019.1.18
 */
public class CaptchaEditText extends LinearLayout implements View.OnClickListener {

    //tools
    private CaptchaEditCallBack mCallBack;
    private Handler mHandler;
    //for timer
    private Timer timerReSendCaptcha;
    private int secondTimer;
    //views
    @BindView(R.id.edt_captcha)
    EditText edtCaptcha;
    @BindView(R.id.tv_get_captcha)
    TextView tvGetCaptcha;

    public CaptchaEditText(Context context) {
        this(context, null);
    }

    public CaptchaEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_captcha_edit_text, this);
        ButterKnife.bind(this);
        tvGetCaptcha.setOnClickListener(this);
    }

    public void setCallBack(CaptchaEditCallBack callBack) {
        this.mCallBack = callBack;
    }

    /**
     * 重新获取验证码倒计时
     */
    public void startCaptchaTimer() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    tvGetCaptcha.setText(secondTimer + "s");
                    secondTimer -= 1;
                    if (secondTimer < 0) {
                        tvGetCaptcha.setText("重新获取");
                        tvGetCaptcha.setEnabled(true);
                        tvGetCaptcha.setTextColor(Color.parseColor("#ff4c88ff"));
                        tvGetCaptcha.setOnClickListener(CaptchaEditText.this);
                        timerReSendCaptcha.cancel();
                    }
                }
            };
        }
        tvGetCaptcha.setEnabled(false);
        tvGetCaptcha.setTextColor(Color.parseColor("#ffC3C3C3"));
        timerReSendCaptcha = new Timer();
        secondTimer = AppConfig.SECOND_RESEND_CAPTCHA;
        timerReSendCaptcha.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        }, 0, 1000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_get_captcha:
                if (mCallBack != null) {
                    mCallBack.getCaptcha();
                }
                break;
        }
    }

    /**
     * 设置TextWatcher
     *
     * @param watcher
     */
    public void addTextChangedListener(TextWatcher watcher) {
        edtCaptcha.addTextChangedListener(watcher);
    }

    /**
     * 设置OnEditorActionListener
     *
     * @param editAction
     */
    public void setOnEditorActionListener(TextView.OnEditorActionListener editAction) {
        edtCaptcha.setOnEditorActionListener(editAction);
    }

    /**
     * 设置ImeOptions
     *
     * @param imeOptions eg:EditorInfo.IME_ACTION_GO
     */
    public void setImeOptions(int imeOptions) {
        edtCaptcha.setImeOptions(imeOptions);
    }

    public String getText() {
        return edtCaptcha.getText().toString();
    }

    public EditText getEditText() {
        return edtCaptcha;
    }

    public interface CaptchaEditCallBack {
        void getCaptcha();
    }
}
