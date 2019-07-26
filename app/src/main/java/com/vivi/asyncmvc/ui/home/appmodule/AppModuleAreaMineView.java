package com.vivi.asyncmvc.ui.home.appmodule;

import android.content.Context;
import android.studio.view.ViewUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.AppModule;

import java.util.List;

import butterknife.BindView;

/**
 * 业务大厅页面:我的应用
 *
 * @author gongwei
 * @Date 2019/1/15
 */
public class AppModuleAreaMineView extends AppModuleAreaView {

    //tools
    private final int SMALL_ICON_COUNT = 5;//默认显示小图标的数量，超过则显示“更多”图标

    //views
    @BindView(R.id.iv_module_area_mine_more)
    ImageView ivModuleAreaMineMore;
    @BindView(R.id.llt_module_area_mine_icon)
    LinearLayout lltModuleAreaMineIcon;
    @BindView(R.id.tv_module_area_edit)
    TextView tvModuleAreaEdit;
    @BindView(R.id.llt_module_area_mine)
    LinearLayout lltModuleAreaMine;

    public AppModuleAreaMineView(@NonNull Context context) {
        this(context, null);
    }

    public AppModuleAreaMineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppModuleAreaMineView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_app_module_mine_area;
    }

    @Override
    protected void initViewForChild() {
        super.initViewForChild();
        setTitle("我的应用");
        closeEditModel();//默认显示小图标（非编辑模式）
    }

    /**
     * 加载我的应用右边的那一排小图标、更多和编辑按钮
     */
    private void showSmallIcon() {
        lltModuleAreaMine.setVisibility(VISIBLE);
        lltModuleAreaMineIcon.removeAllViews();

        List<AppModule> myModules = AppModule.getMyModules();
        int iteratorSize;
        if (myModules.size() > SMALL_ICON_COUNT) {
            ivModuleAreaMineMore.setVisibility(VISIBLE);
            iteratorSize = SMALL_ICON_COUNT;
        } else {
            ivModuleAreaMineMore.setVisibility(GONE);
            iteratorSize = myModules.size();
        }
        for (int i = 0; i < iteratorSize; i++) {
            AppModule temp = myModules.get(i);
            ImageView icon = new ImageView(mContext);
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            icon.setImageResource(temp.module.icRes);
            LinearLayout.LayoutParams iconParam = new LinearLayout.LayoutParams(0, ViewUtils.dip2px(mContext, 24));
            iconParam.weight = 1;
            lltModuleAreaMineIcon.addView(icon, iconParam);
        }
    }

    /**
     * 隐藏我的应用右边的那一排小图标、更多和编辑按钮
     */
    private void hideSmallIcon() {
        lltModuleAreaMine.setVisibility(GONE);
    }

    /**
     * 编辑按钮点击回调
     *
     * @param listener
     */
    public void setEditClickListener(final OnClickListener listener) {
        tvModuleAreaEdit.setOnClickListener(listener);
    }

    /**
     * 初始化“我的应用”时，和需要刷新“我的应用”时，都调用此方法
     */
    public void openEditModel(List<AppModule> myModules, AppModuleView.AppModuleCallBack callBack) {
        hideSmallIcon();
        setModules(myModules, callBack);
        lltModuleAreaContent.setVisibility(VISIBLE);
        int lineCount = lltModuleAreaContent.getChildCount();
        for (int i = 0; i < lineCount; i++) {
            LinearLayout line = (LinearLayout) lltModuleAreaContent.getChildAt(i);
            int moduleCount = line.getChildCount();
            for (int j = 0; j < moduleCount; j++) {
                View child = line.getChildAt(j);
                if (child instanceof AppModuleView) {
                    ((AppModuleView) child).openEditModel(true);
                }
            }
        }
    }

    /**
     * 初始化小图标，和刷新小图标时都调用此方法
     */
    public void closeEditModel() {
        showSmallIcon();
        lltModuleAreaContent.setVisibility(GONE);
    }
}
