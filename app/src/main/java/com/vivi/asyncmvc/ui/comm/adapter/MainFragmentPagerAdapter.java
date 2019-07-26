package com.vivi.asyncmvc.ui.comm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> list;

    public MainFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
    }

    @Override
    public Fragment getItem(int indext) {
        return list.get(indext);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
