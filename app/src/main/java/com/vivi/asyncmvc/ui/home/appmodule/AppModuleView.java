package com.vivi.asyncmvc.ui.home.appmodule;

import android.content.Context;
import android.studio.view.ViewUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.AppModule;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 单个App功能模块View
 *
 * @author gongwei
 * @Date 2019.1.17
 */
public class AppModuleView extends FrameLayout {

    private AppModuleCallBack mCallBack;
    private AppModule mAppModule;

    @BindView(R.id.iv_module_icon)
    ImageView ivModuleIcon;
    @BindView(R.id.iv_module_controller)
    ImageView ivModuleController;
    @BindView(R.id.tv_module_name)
    TextView tvModuleName;
    @BindView(R.id.llt_module_root)
    LinearLayout lltModuleRoot;

    public AppModuleView(@NonNull Context context) {
        this(context, null);
    }

    public AppModuleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppModuleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_app_module, this);
        ButterKnife.bind(this);
        ivModuleController.setVisibility(GONE);
    }

    /**
     * 设置数据
     *
     * @param appModule
     */
    public void setModule(AppModule appModule, AppModuleCallBack callBack) {
        this.mCallBack = callBack;
        this.mAppModule = appModule;

        if (appModule != null) {
            ivModuleIcon.setImageResource(appModule.module.icRes);
            tvModuleName.setText(appModule.module.name);
        }
        lltModuleRoot.setClickable(true);
        lltModuleRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.clickModule(mAppModule);
                }
            }
        });
    }

    public void setPaddingBottom(int bottom) {
        lltModuleRoot.setPadding(0, 0, 0, ViewUtils.dip2px(getContext(), bottom));
    }

    public void openEditModel() {
        boolean isInMyModule = AppModule.isImMyModules(mAppModule);
        openEditModel(isInMyModule);
    }

    /**
     * 打开编辑模式
     *
     * @param isInMyModule 是否存在于“我的应用”中
     */
    public void openEditModel(boolean isInMyModule) {
        lltModuleRoot.setClickable(false);
        ivModuleController.setVisibility(VISIBLE);
        ivModuleController.setImageResource(isInMyModule ? R.drawable.ic_module_del : R.drawable.ic_module_add);
        ivModuleController.setTag(isInMyModule);
        ivModuleController.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    boolean isInMyModule = (boolean) ivModuleController.getTag();
                    boolean result = mCallBack.clickController(AppModuleView.this, mAppModule, !isInMyModule);
                    if (result) {
                        ivModuleController.setImageResource(isInMyModule ? R.drawable.ic_module_add : R.drawable.ic_module_del);
                        ivModuleController.setTag(!isInMyModule);
                    }
                }
            }
        });
    }

    /**
     * 关闭编辑模式
     */
    public void closeEditModel() {
        lltModuleRoot.setClickable(true);
        ivModuleController.setVisibility(GONE);
        ivModuleController.setOnClickListener(null);
    }

    public interface AppModuleCallBack {
        void clickModule(AppModule appModule);

        /**
         * @param appModule
         * @param actionAdd 点击的添加还是删除，true：添加 false：删除
         * @return 此次执行是否成功完成
         */
        boolean clickController(AppModuleView view, AppModule appModule, boolean actionAdd);
    }
}
