package com.vivi.asyncmvc.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.studio.view.ViewUtils;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.BusinessType;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.view.dialog.ListDialog;
import com.vivi.asyncmvc.comm.view.edit.CountEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultRow;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.noober.background.drawable.DrawableCreator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 温馨服务页面
 *
 * @author gongwei
 * @date 2019/2/13
 */
public class ConsultActivity extends BaseActivity {

    @BindView(R.id.tv_consult_business_type)
    TextView tvBusinessType;
    @BindView(R.id.llt_consult_question_type)
    LinearLayout lltQuestionType;
    @BindView(R.id.edt_consult)
    CountEditText edtConsult;
    @BindView(R.id.btn_consult_commit)
    Button btnCommit;

    //data
    private final int DESCRIPTION_MAX_LENGTH = 200;
    private final int SINGLE_ROW_MAX_COL = 3;//问题类型一排放3个
    private String businessCode, businessName;//所选业务类型
    private String questionCode, questionName;//所选问题类型
    private String strDescription;//问题描述
    private List<BusinessType> mBusinessTypeList;//备选类型数据
    private List<TextView> mQuestionTypeTextViews = new ArrayList<>();//问题类型的选项缓存一个List，在切换时遍历使用
    //tools
    private Drawable drawableChecked;
    private Drawable drawableUnChecked;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_consult;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, ConsultActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("温馨服务");

        //init tools
        int dp3 = ViewUtils.dip2px(this, 3);
        drawableChecked = new DrawableCreator.Builder()
                .setSolidColor(getResources().getColor(R.color.white))
                .setCornersRadius(dp3)
                .setStrokeWidth(2)
                .setStrokeColor(Color.parseColor("#4C88FF"))
                .build();
        drawableUnChecked = new DrawableCreator.Builder()
                .setSolidColor(getResources().getColor(R.color.white))
                .setCornersRadius(dp3)
                .setStrokeWidth(2)
                .setStrokeColor(Color.parseColor("#CCCCCC"))
                .build();
        //init views
        edtConsult.init("请描述您遇到的问题", DESCRIPTION_MAX_LENGTH);
        edtConsult.setEditHeight(ViewUtils.dip2px(this, 168));
        edtConsult.addTextChangedListener(new EditTextWatcher(edtConsult, new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                strDescription = text;
                refreshBtnCommit();
            }
        }));
        btnCommit.setEnabled(false);
    }

    private void refreshBtnCommit() {
        btnCommit.setEnabled(!TextUtils.isEmpty(strDescription));
    }

    /**
     * 获取业务类型
     */
    private void dealBusinessType() {
        if (mBusinessTypeList == null) {
            showLoadingDialog();
            Api.getBusinessType(new JsonResultCallback<JsonResultRow<BusinessType>>() {
                @Override
                public void onSuccess(int statusCode, JsonResultRow<BusinessType> response, int tag) {
                    dismissLoadingDialog();
                    mBusinessTypeList = response.getData();
                    showBusinessTypeListSelect();
                }

                @Override
                public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                    super.onFailure(statusCode, responseString, throwable, tag);
                    dismissLoadingDialog();
                    UI.showToast(responseString);
                }
            });
        } else {
            showBusinessTypeListSelect();
        }
    }

    /**
     * 弹出业务类型列表选择器
     */
    private void showBusinessTypeListSelect() {
        if (mBusinessTypeList != null && !mBusinessTypeList.isEmpty()) {
            UI.showListDialog(this, BusinessType.getNameList(mBusinessTypeList), "问题类型", new ListDialog.ListDialogCallback() {
                @Override
                public void choose(int which) {
                    //data
                    BusinessType temp = mBusinessTypeList.get(which);
                    businessCode = temp.businessTypeCode;
                    businessName = temp.businessTypeName;
                    questionCode = null;
                    questionName = null;
                    //view
                    tvBusinessType.setText(businessName);
                    tvBusinessType.setTextColor(Color.parseColor("#4D4D4D"));
                    showQuestionType(temp.questionTypes);
                    refreshBtnCommit();
                }
            });
        } else {
            UI.showToast("抱歉，未获取到问题类型");
        }
    }

    /**
     * 显示问题类型
     *
     * @param questionTypes
     */
    private void showQuestionType(List<BusinessType.QuestionType> questionTypes) {
        lltQuestionType.removeAllViews();
        mQuestionTypeTextViews.clear();
        if (questionTypes == null || questionTypes.isEmpty()) {
            lltQuestionType.setVisibility(View.GONE);
            return;
        }

        lltQuestionType.setVisibility(View.VISIBLE);
        LinearLayout lltRow = null;
        LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParam.weight = 1;
        int size = questionTypes.size();
        for (int i = 0; i < size; i++) {
            //new line
            if (i % SINGLE_ROW_MAX_COL == 0) {
                lltRow = new LinearLayout(this);
                lltRow.setWeightSum(SINGLE_ROW_MAX_COL);
                lltQuestionType.addView(lltRow);
            }
            //new item
            final BusinessType.QuestionType temp = questionTypes.get(i);

            int dp6 = ViewUtils.dip2px(this, 6);
            int dp7 = ViewUtils.dip2px(this, 7);
            TextView tvItem = newQuestionTypeItem(temp.questionTypeName);
            tvItem.setPadding(0, dp6, 0, dp6);
            tvItem.setTag(temp);
            //默认选中第一个
            if (i == 0) {
                questionCode = temp.questionTypeCode;
                questionName = temp.questionTypeName;
                tvItem.setBackground(drawableChecked);
                tvItem.setTextColor(Color.parseColor("#4C88FF"));
            } else {
                tvItem.setBackground(drawableUnChecked);
            }
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchQuestionType(v);
                }
            });
            mQuestionTypeTextViews.add(tvItem);

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setPadding(dp7, 0, dp7, 0);
            itemLayout.addView(tvItem, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            lltRow.addView(itemLayout, itemParam);
        }
    }

    /**
     * 切换选中的问题类型
     *
     * @param v
     */
    private void switchQuestionType(View v) {
        for (TextView item : mQuestionTypeTextViews) {
            if (item == v) {
                item.setTextColor(Color.parseColor("#4C88FF"));
                item.setBackground(drawableChecked);
                BusinessType.QuestionType tag = (BusinessType.QuestionType) v.getTag();
                if (tag != null) {
                    questionCode = tag.questionTypeCode;
                    questionName = tag.questionTypeName;
                }
            } else {
                item.setTextColor(Color.parseColor("#4D4D4D"));
                item.setBackground(drawableUnChecked);
            }
        }
    }

    /**
     * new 问题类型的TextView
     *
     * @param str
     * @return
     */
    private TextView newQuestionTypeItem(String str) {
        TextView tvItem = new TextView(this);
        tvItem.setText(str);
        tvItem.setGravity(Gravity.CENTER);
        tvItem.setTextSize(14);
        return tvItem;
    }

    private void commit() {
        OS.hideSoftKeyboard(this);
        if (strDescription.length() > DESCRIPTION_MAX_LENGTH) {
            UI.showToast(String.format("输入内容不能超过%s字", DESCRIPTION_MAX_LENGTH));
        } else if (TextUtils.isEmpty(businessCode) || TextUtils.isEmpty(questionCode)) {
            UI.showToast("请选择问题类型");
        } else {
            showLoadingDialog();
            Api.questionCommit(businessCode, businessName, questionCode, questionName, strDescription, new JsonResultCallback<JsonResultVoid>() {
                @Override
                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                    dismissLoadingDialog();
                    UI.showConfirmDialog(ConsultActivity.this, false, "您的意见已成功提交\n感谢您的反馈", "确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                    super.onFailure(statusCode, responseString, throwable, tag);
                    dismissLoadingDialog();
                    UI.showToast(responseString);
                }
            });
        }
    }

    @OnClick({R.id.llt_consult_call, R.id.llt_consult_business_type, R.id.btn_consult_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llt_consult_call:
                UI.makeCall(this, AppConfig.SERVICE_CALL_DEFAULT);
                break;
            case R.id.llt_consult_business_type:
                dealBusinessType();
                break;
            case R.id.btn_consult_commit:
                commit();
                break;
        }
    }
}