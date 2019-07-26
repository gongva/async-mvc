package com.vivi.asyncmvc.ui.me;

import android.Manifest;
import android.os.Bundle;
import android.studio.util.DateUtils;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.UserInfo;
import com.vivi.asyncmvc.base.ActivitySupport;
import com.vivi.asyncmvc.base.BaseFragment;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.view.dialog.ActionSheet;
import com.vivi.asyncmvc.comm.view.roundimg.RoundedImageView;
import com.vivi.asyncmvc.comm.view.scroll.ImageEnlargeScrollView;
import com.vivi.asyncmvc.ui.comm.selectimg.MultiImageSelectorActivity;
import com.vivi.asyncmvc.library.plugs.glide.AImage;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.utils.CommonTools;
import com.vivi.asyncmvc.library.utils.LogCat;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的Fragment
 */
public class MeFragment extends BaseFragment {
    @BindView(R.id.tv_me_name_float)
    TextView tvMeNameFloat;
    @BindView(R.id.flt_me_nav)
    FrameLayout fltMeNav;
    @BindView(R.id.sv_me_root)
    ImageEnlargeScrollView svMeRoot;
    @BindView(R.id.iv_me_cover)
    ImageView ivMeCover;
    @BindView(R.id.tv_me_credit)
    TextView tvMeCredit;
    @BindView(R.id.tv_me_score)
    TextView tvMeScore;
    @BindView(R.id.tv_me_score_unit)
    TextView tvMeScoreUnit;
    @BindView(R.id.iv_me_head)
    RoundedImageView ivMeHead;
    @BindView(R.id.tv_me_name)
    TextView tvMeName;
    @BindView(R.id.llt_me_id_card)
    LinearLayout lltMeIdCard;
    @BindView(R.id.tv_me_id_card)
    TextView tvMeIdCard;
    @BindView(R.id.iv_me_id_card_arrow)
    ImageView ivMeIdCardArrow;
    @BindView(R.id.tv_me_user_type)
    TextView tvMeUserType;
    @BindView(R.id.tv_me_register_time)
    TextView tvMeRegisterTime;
    @BindView(R.id.tv_me_phone)
    TextView tvMePhone;
    @BindView(R.id.tv_me_email)
    TextView tvMeEmail;
    @BindView(R.id.tv_me_address)
    TextView tvMeAddress;

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideActionBar();

        initView();
    }

    private void initView() {
        ViewGroup.LayoutParams coverParam = ivMeCover.getLayoutParams();
        coverParam.height = OS.getHeightByRatioAndScreenWidth(getActivity(), 2 / 1);
        ivMeCover.setLayoutParams(coverParam);
        svMeRoot.setPullEnlarge(ivMeCover, 2 / 1);
        svMeRoot.setViewFaded(fltMeNav);
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
        getUserInfo();
    }

    private void setData() {
        UserInfo userInfo = LoginManager.getInstance().getUserInfo();
        if (userInfo != null) {
            LogCat.i(userInfo.toString());
            AImage.load(userInfo.avatar, ivMeHead);
            tvMeName.setText(userInfo.username);
            tvMeNameFloat.setText(userInfo.username);
            tvMeUserType.setText(userInfo.userType);
            tvMeRegisterTime.setText(DateUtils.formatDate(new Date(userInfo.registerTime)));
            tvMePhone.setText(userInfo.phone);
            tvMeEmail.setText(userInfo.email);
            tvMeAddress.setText(userInfo.address);
            if (userInfo.hasDriverLicense) {
                lltMeIdCard.setClickable(false);
                tvMeIdCard.setText(userInfo.idCardNum);
                ivMeIdCardArrow.setVisibility(View.GONE);
                tvMeCredit.setText(TextUtils.isEmpty(userInfo.trafficCredit) ? "- -" : userInfo.trafficCredit);
                tvMeScore.setText(String.valueOf(userInfo.driverLicenseAvailableScore));
                tvMeScoreUnit.setText("分");
            } else {
                lltMeIdCard.setClickable(true);
                tvMeIdCard.setText("未绑定");
                ivMeIdCardArrow.setVisibility(View.VISIBLE);
                tvMeCredit.setText("- -");
                tvMeScore.setText("- -");
                tvMeScoreUnit.setText("");
            }

        }
    }

    private void getUserInfo() {
        Api.getUserInfo(new JsonResultCallback<JsonResult<UserInfo>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<UserInfo> response, int tag) {
                LoginManager.getInstance().saveUserInfo(response.getData());
                setData();
            }
        });
    }

    /**
     * 设置头像
     *
     * @param avatar
     */
    public void setAvatar(String avatar) {
        LoginManager.getInstance().getUserInfo().avatar = avatar;
        LoginManager.getInstance().save();
        setData();
    }

    @OnClick({R.id.llt_me_credit, R.id.llt_me_score, R.id.iv_me_head, R.id.llt_me_id_card, R.id.llt_me_user_type,
            R.id.llt_me_register_time, R.id.llt_me_collection, R.id.lly_me_phone, R.id.llt_me_email,
            R.id.llt_me_address, R.id.iv_me_setting})
    public void onClick(View view) {
        UserInfo userInfo = LoginManager.getInstance().getUserInfo();
        switch (view.getId()) {
            case R.id.llt_me_credit:
                if (userInfo.hasDriverLicense) {
                    UI.showToast("跳转信用");
                } else {
                    UI.showConfirmDialog(getActivity(), "温馨提示","请申领电子驾照", "取消", null, "去申领", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UI.showToast("申领");
                        }
                    });
                }
                break;
            case R.id.llt_me_score:
                if (userInfo.hasDriverLicense) {
                    UI.showToast("跳转剩余记分列表");
                } else {
                    UI.showConfirmDialog(getActivity(), "温馨提示","请申领电子驾照", "取消", null, "去申领", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UI.showToast("申领");
                        }
                    });
                }
                break;
            case R.id.iv_me_head:
                ActivitySupport.requestRunPermission(getActivity(), new String[]{Manifest.permission.CAMERA}, new PermissionRequestListener() {
                    @Override
                    public void onGranted() {
                        ActionSheet.create(getActivity())
                                .setCancelable(false)
                                .setCanceledOnTouchOutside(true)
                                .addSheetItem("拍照", new ActionSheet.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int index) {
                                        CommonTools.openCamera(getActivity());
                                    }
                                })
                                .addSheetItem("选取相册", new ActionSheet.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        MultiImageSelectorActivity.startForFastChoose(getActivity());
                                    }
                                }).show();
                    }
                });
                break;
            case R.id.llt_me_id_card:
                if (!userInfo.hasDriverLicense) {
                    UI.showConfirmDialog(getActivity(), "温馨提示","请申领电子驾照", "取消", null, "去申领", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UI.showToast("申领");
                        }
                    });
                }
                break;
            case R.id.llt_me_user_type:
                break;
            case R.id.llt_me_register_time:
                break;
            case R.id.llt_me_collection:
                break;
            case R.id.lly_me_phone:
                ChangePhoneActivity.start(getActivity());
                break;
            case R.id.llt_me_email:
                ChangeMailActivity.start(getActivity());
                break;
            case R.id.llt_me_address:
                AddressListActivity.start(getActivity());
                break;
            case R.id.iv_me_setting:
                SettingActivity.start(getActivity());
                break;
        }
    }
}