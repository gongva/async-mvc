package com.vivi.asyncmvc.ui.home.homepage;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vivi.asyncmvc.api.entity.AppModule;
import com.vivi.asyncmvc.comm.view.banner.LoopViewPager;
import com.vivi.asyncmvc.ui.home.appmodule.AppModuleView;

import java.util.ArrayList;
import java.util.List;

public class HomeModuleBannerAdapter extends LoopViewPager.BannerPagerAdapter {

    //tools
    public final int SINGLE_ROW_MAX_COL = 4;//一排放4个
    private HomeModuleCallBack mCallBack;
    //data
    private List<AppModule> myModules = new ArrayList<>();

    @Override
    public void clear() {
        myModules.clear();
    }

    /**
     * @param objects objects[0]:HomeModuleCallBack
     */
    @Override
    public void setData(Object... objects) {
        this.mCallBack = (HomeModuleCallBack) objects[0];
        refresh();
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        myModules.clear();
        myModules.addAll(AppModule.getMyModules());
        myModules.add(new AppModule(AppModule.AppModuleType.MODULE_MORE));//末尾增加一个更多
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int realSize = myModules.size();
        int count = realSize / SINGLE_ROW_MAX_COL;
        if (realSize % SINGLE_ROW_MAX_COL != 0) {
            count++;
        }
        return count;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LinearLayout line = new LinearLayout(container.getContext());
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setWeightSum(SINGLE_ROW_MAX_COL);

        AppModuleView.AppModuleCallBack callBack = new AppModuleView.AppModuleCallBack() {

            @Override
            public void clickModule(AppModule appModule) {
                if (mCallBack != null) {
                    mCallBack.onClick(appModule);
                }
            }

            @Override
            public boolean clickController(AppModuleView view, AppModule appModule, boolean actionAdd) {
                return false;
            }
        };

        int lineSizeStart = position * SINGLE_ROW_MAX_COL;//当前position在myModules中的数据下标起点
        int lineSizeEnd = Math.min((position + 1) * SINGLE_ROW_MAX_COL, myModules.size());//下标终点：不包含
        for (int i = lineSizeStart; i < lineSizeEnd; i++) {
            AppModuleView moduleView = new AppModuleView(container.getContext());
            moduleView.setModule(myModules.get(i), callBack);
            moduleView.setPaddingBottom(0);
            LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            itemParam.weight = 1;
            line.addView(moduleView, itemParam);
        }
        container.addView(line);
        return line;
    }

    @Override
    public boolean canCyclePlay() {
        return false;
    }

    public interface HomeModuleCallBack {
        void onClick(AppModule appModule);
    }
}
