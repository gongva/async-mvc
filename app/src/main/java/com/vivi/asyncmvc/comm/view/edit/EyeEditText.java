package com.vivi.asyncmvc.comm.view.edit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;

import com.vivi.asyncmvc.R;

/**
 * 带眼睛的EditText
 *
 * @Author gongwei
 * @Date 2019.1.17
 */
public class EyeEditText extends AppCompatEditText {
    /**
     * 眼睛按钮的引用
     */
    private Drawable mEyeOnDrawable;
    private Drawable mEyeOffDrawable;

    public EyeEditText(Context context) {
        this(context, null);
    }

    public EyeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EyeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //默认设置为Password
        setPasswordType(true);

        mEyeOnDrawable = getResources().getDrawable(R.drawable.ic_eye_on);
        mEyeOffDrawable = getResources().getDrawable(R.drawable.ic_eye_off);
        mEyeOnDrawable.setBounds(0, 0, mEyeOnDrawable.getIntrinsicWidth(), mEyeOnDrawable.getIntrinsicHeight());
        mEyeOffDrawable.setBounds(0, 0, mEyeOffDrawable.getIntrinsicWidth(), mEyeOffDrawable.getIntrinsicHeight());

        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mEyeOffDrawable, getCompoundDrawables()[3]);
    }

    /**
     * 设置该EditText为Password or Text
     *
     * @param isPassword true:Password, false:Text
     */
    private void setPasswordType(boolean isPassword) {
        if (isPassword) {
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 - 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 -
     * 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    if (getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        setPasswordType(false);
                        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mEyeOnDrawable, getCompoundDrawables()[3]);
                    } else {
                        setPasswordType(true);
                        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mEyeOffDrawable, getCompoundDrawables()[3]);
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }
}
