package com.vivi.asyncmvc.comm.view.edit;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.library.utils.UI;

/**
 * 带字数限制的模拟EditText
 *
 * @author gongwei
 * @date 2019.1.30
 */
public class CountEditText extends FrameLayout {

    private int MAX_LENGTH_CONTENT;
    private TextWatcher mWatcher;

    private Context mContext;
    private EditText edtContent;
    private TextView tvCount;

    public CountEditText(Context context) {
        this(context, null);
    }

    public CountEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_count_edit_text, this);
        edtContent = findViewById(R.id.edt_count_content);
        tvCount = findViewById(R.id.edt_count);
    }

    public void init(String hint, int maxLength) {
        init(hint, maxLength, "");
    }

    public void init(String hint, int maxLength, String textDefault) {
        MAX_LENGTH_CONTENT = maxLength;
        edtContent.setHint(hint);
        edtContent.setTextColor(Color.parseColor("#505050"));
        tvCount.setTextColor(Color.parseColor("#969696"));
        String strTemp = String.format("0/%s", String.valueOf(MAX_LENGTH_CONTENT));
        tvCount.setText(strTemp);

        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mWatcher != null) {
                    mWatcher.beforeTextChanged(charSequence, i, i1, i2);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int lengthCount = edtContent.getText().toString().length();
                if (MAX_LENGTH_CONTENT - lengthCount >= 0) {//自己设计的：剩余字数为负后，计数变为的红色，否则都为灰色
                    String strTemp = String.format("%s/%s", String.valueOf(lengthCount), String.valueOf(MAX_LENGTH_CONTENT));
                    tvCount.setText(strTemp);
                } else {
                    String strTemp = String.format(mContext.getResources().getString(R.string.count_edit_text_count), String.valueOf(lengthCount), String.valueOf(MAX_LENGTH_CONTENT));
                    tvCount.setText(Html.fromHtml(strTemp));
                }
                if (mWatcher != null) {
                    mWatcher.onTextChanged(charSequence, i, i1, i2);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mWatcher != null) {
                    mWatcher.afterTextChanged(editable);
                }
            }
        });
        setText(textDefault);
    }

    /**
     * 设置TextWatcher
     *
     * @param watcher
     */
    public void addTextChangedListener(TextWatcher watcher) {
        mWatcher = watcher;
    }

    public void setEditHeight(int height) {
        edtContent.setHeight(height);
    }

    public void setEditMaxHeight(int height) {
        edtContent.setMaxHeight(height);
    }

    public void setText(CharSequence text) {
        UI.setEditTextDefault(edtContent, text);
    }

    public Editable getText() {
        return edtContent.getText();
    }

    public void requestFocusForContent() {
        edtContent.requestFocus();
    }

    public EditText getEditText() {
        return edtContent;
    }

    public boolean verifyLengthError() {
        return edtContent.getText().toString().length() > MAX_LENGTH_CONTENT;
    }
}
