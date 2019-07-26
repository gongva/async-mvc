package com.vivi.asyncmvc.ui.car;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseFragment;

/**
 * 爱车Fragment
 */
public class CarFragment extends BaseFragment {
    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_car;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideActionBar();
        initView();
    }

    private void initView() {

    }
}
