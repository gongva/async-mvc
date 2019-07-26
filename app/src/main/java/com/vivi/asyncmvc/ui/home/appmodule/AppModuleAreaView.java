package com.vivi.asyncmvc.ui.home.appmodule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.AppModule;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 业务大厅页面，多个AppModule为一个集合的View，eg：驾驶证、机动车、出行周边
 *
 * @author gongwei
 * @Date 2019/1/14
 */
public class AppModuleAreaView extends FrameLayout {

    public static final int SINGLE_ROW_MAX_COL = 4;//一排默认放4个Module

    protected Context mContext;
    protected AppModuleView.AppModuleCallBack mCallBack;

    @BindView(R.id.tv_module_area_title)
    TextView tvModuleAreaTitle;
    @BindView(R.id.llt_module_area_content)
    LinearLayout lltModuleAreaContent;

    public AppModuleAreaView(@NonNull Context context) {
        this(context, null);
    }

    public AppModuleAreaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppModuleAreaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 布局文件ID
     *
     * @return
     */
    protected int getLayoutId() {
        return R.layout.view_app_module_area;
    }

    protected void initView(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(getLayoutId(), this);
        ButterKnife.bind(this);
        initViewForChild();
    }

    /**
     * 初始化数据
     *
     * @param title
     * @param appModuleList
     */
    public void setModules(String title, List<AppModule> appModuleList, AppModuleView.AppModuleCallBack callBack) {
        setTitle(title);
        setModules(appModuleList, callBack);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    protected void setTitle(String title) {
        tvModuleAreaTitle.setText(title);
    }

    /**
     * 设置Modules数据
     *
     * @param appModuleList
     * @param callBack
     */
    protected void setModules(List<AppModule> appModuleList, AppModuleView.AppModuleCallBack callBack) {
        this.mCallBack = callBack;
        addLine(appModuleList);
    }

    private void addLine(List<AppModule> list) {
        lltModuleAreaContent.removeAllViews();
        if (list == null) return;
        LinearLayout lltRow = null;
        LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParam.weight = 1;
        for (int i = 0; i < list.size(); i++) {
            if (i % SINGLE_ROW_MAX_COL == 0) {
                lltRow = new LinearLayout(mContext);
                lltRow.setWeightSum(SINGLE_ROW_MAX_COL);
                lltModuleAreaContent.addView(lltRow);
            }
            AppModuleView item = new AppModuleView(mContext);
            item.setModule(list.get(i), mCallBack);
            lltRow.addView(item, itemParam);
        }
    }

    /**
     * 打开编辑模式
     */
    public void openEditModel() {
        int lineCount = lltModuleAreaContent.getChildCount();
        for (int i = 0; i < lineCount; i++) {
            LinearLayout line = (LinearLayout) lltModuleAreaContent.getChildAt(i);
            int moduleCount = line.getChildCount();
            for (int j = 0; j < moduleCount; j++) {
                View child = line.getChildAt(j);
                if (child instanceof AppModuleView) {
                    ((AppModuleView) child).openEditModel();
                }
            }
        }
    }

    /**
     * 关闭编辑模式
     */
    public void closeEditModel() {
        int lineCount = lltModuleAreaContent.getChildCount();
        for (int i = 0; i < lineCount; i++) {
            LinearLayout line = (LinearLayout) lltModuleAreaContent.getChildAt(i);
            int moduleCount = line.getChildCount();
            for (int j = 0; j < moduleCount; j++) {
                View child = line.getChildAt(j);
                if (child instanceof AppModuleView) {
                    ((AppModuleView) child).closeEditModel();
                }
            }
        }
    }

    public LinearLayout getLltModuleAreaContent() {
        return lltModuleAreaContent;
    }

    /**
     * 开给子类，用于子view扩展布局的方法
     * 无扩展view则无需重写
     */
    protected void initViewForChild() {
    }
}
