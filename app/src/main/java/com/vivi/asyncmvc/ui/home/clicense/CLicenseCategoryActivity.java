package com.vivi.asyncmvc.ui.home.clicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.view.errorpage.ErrorPage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 备案机动车列表（本人/非本人机动车列表，最多各3条）
 *
 * @author gongwei
 * @date 2019/2/13
 */
public class CLicenseCategoryActivity extends BaseActivity {

    @BindView(R.id.ep_c_category)
    ErrorPage epCList;
    @BindView(R.id.llt_c_category_owner_content)
    LinearLayout lltOwnerContent;
    @BindView(R.id.llt_c_category_owner)
    LinearLayout lltOwner;
    @BindView(R.id.llt_c_category_not_owner_content)
    LinearLayout lltNotOwnerContent;
    @BindView(R.id.llt_c_category_not_owner)
    LinearLayout lltNotOwner;
    //data
    private List<CarLicense> listOwner = new ArrayList<>();//本人机动车
    private List<CarLicense> listNotOwner = new ArrayList<>();//非本人机动车
    //tools
    private final int ITEM_MAX_LINE = 3;//最多显示机动车的条数
    private LayoutInflater mInflater;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_clicense_category;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CLicenseCategoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("备案机动车");

        mInflater = LayoutInflater.from(this);
        initMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initMenu() {
        setMenu("备案记录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindRecordActivity.start(CLicenseCategoryActivity.this);
            }
        });
    }

    /**
     * 初始化数据
     * 拆分本人/非本人机动车
     */
    private void initData() {
        List<CarLicense> licensesAll = CarLicense.queryAll();
        if (licensesAll == null || licensesAll.size() == 0) {
            epCList.showErrorPage("您还未备案机动车哦");
            lltOwner.setVisibility(View.GONE);
            lltNotOwner.setVisibility(View.GONE);
        } else {
            listOwner.clear();
            listNotOwner.clear();
            for (CarLicense license : licensesAll) {
                if (license.isOwner) {
                    listOwner.add(license);
                } else {
                    listNotOwner.add(license);
                }
            }
            initCars();
        }
    }

    /**
     * 显示车辆列表
     */
    private void initCars() {
        //本人机动车
        if (listOwner.size() == 0) {
            lltOwner.setVisibility(View.GONE);
        } else {
            lltOwner.setVisibility(View.VISIBLE);
            lltOwnerContent.removeAllViews();
            int size = listOwner.size();
            size = size > ITEM_MAX_LINE ? ITEM_MAX_LINE : size;
            for (int i = 0; i < size; i++) {
                View item = initItem(i, listOwner.get(i));
                lltOwnerContent.addView(item);
            }
        }
        //非本人机动车
        if (listNotOwner.size() == 0) {
            lltNotOwner.setVisibility(View.GONE);
        } else {
            lltNotOwner.setVisibility(View.VISIBLE);
            lltNotOwnerContent.removeAllViews();
            int size = listNotOwner.size();
            size = size > ITEM_MAX_LINE ? ITEM_MAX_LINE : size;
            for (int i = 0; i < size; i++) {
                View item = initItem(i, listNotOwner.get(i));
                lltNotOwnerContent.addView(item);
            }
        }
    }

    private View initItem(int index, CarLicense license) {
        View item = mInflater.inflate(R.layout.item_clicense_category, null);
        item.findViewById(R.id.v_c_category_line).setVisibility(index == 0 ? View.GONE : View.VISIBLE);
        TextView platNumber = item.findViewById(R.id.tv_c_category_plate_number);
        TextView name = item.findViewById(R.id.tv_c_category_name);
        TextView illegal = item.findViewById(R.id.tv_c_category_illegal);
        TextView legal = item.findViewById(R.id.tv_c_category_legal);
        platNumber.setText(license.plateNum);
        name.setText(license.ownerName);
        if (license.unhandleCount > 0) {
            //有违法未处理记录
            illegal.setVisibility(View.VISIBLE);
            legal.setVisibility(View.GONE);
        } else {
            //无法未处理记录
            illegal.setVisibility(View.GONE);
            legal.setVisibility(View.VISIBLE);
        }
        item.setTag(license);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarLicense temp = (CarLicense) v.getTag();
                CLicenseListActivity.startForSingle(CLicenseCategoryActivity.this, temp.id);
            }
        });
        return item;
    }

    @OnClick({R.id.tv_c_category_mine_more, R.id.tv_c_category_not_mine_more, R.id.tv_c_category_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_c_category_mine_more:
                CLicenseListActivity.startForOwner(this);
                break;
            case R.id.tv_c_category_not_mine_more:
                CLicenseListActivity.startForNotOwner(this);
                break;
            case R.id.tv_c_category_add:
                CLicenseBindActivity.start(CLicenseCategoryActivity.this);
                break;
        }
    }
}
