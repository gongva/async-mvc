package com.vivi.asyncmvc.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.Address;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.SaveAddressEvent;
import com.vivi.asyncmvc.comm.view.listview.ListViewDataAdapter;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderBase;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderCreator;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultRow;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.UI;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 收货地址列表
 *
 * @author gongwei
 * @date 2019.1.23
 */
public class AddressListActivity extends BaseActivity {

    @BindView(R.id.lv_address_list)
    ListView lvAddressList;
    @BindView(R.id.rfl_address_list)
    SmartRefreshLayout rflAddressList;

    //tools
    private ListViewDataAdapter<Address> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_address_list;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, AddressListActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.bindLifecycle(this);
        setTitle("收货信息");
        initView();
    }

    private void initView() {
        //refresh
        rflAddressList.setEnableLoadMore(false);
        rflAddressList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getAddress();
            }
        });
        //list view
        mAdapter = new ListViewDataAdapter<>(new ViewHolderCreator<Address>() {
            @Override
            public ViewHolderBase<Address> createViewHolder() {
                return new AddressViewHolder();
            }
        });
        lvAddressList.setAdapter(mAdapter);

        showInitLoading();
        getAddress();
    }

    private void getAddress() {
        Api.addressList(new JsonResultCallback<JsonResultRow<Address>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultRow<Address> response, int tag) {
                dismissLoadingDialog();
                mAdapter.getDataList().clear();
                mAdapter.getDataList().addAll(response.getData());
                mAdapter.notifyDataSetChanged();
                rflAddressList.finishRefresh();
                if (mAdapter.getDataList().isEmpty()) {
                    showErrorPage("您还未新增收货信息哦", "新增收货信息", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AddressAddActivity.start(AddressListActivity.this);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                dismissLoadingDialog();
                rflAddressList.finishRefresh();
                if (mAdapter.isEmpty()) {
                    showErrorPageForHttp(statusCode, responseString);
                } else {
                    UI.showToast(responseString);
                }
            }
        });
    }

    /**
     * 本地设置默认地址，并更新UI
     *
     * @param id
     */
    private void setAddressDefault(String id) {
        for (Address address : mAdapter.getDataList()) {
            address.isDefault = address.id.equals(id);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 本地删除地址并更新UI
     *
     * @param id
     */
    private void deleteAddress(String id) {
        for (Address address : mAdapter.getDataList()) {
            if (address.id.equals(id)) {
                mAdapter.getDataList().remove(address);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_address_add)
    public void onClick() {
        AddressAddActivity.start(this);
    }

    @Subscribe
    public void onEvent(SaveAddressEvent event) {
        getAddress();
    }

    public class AddressViewHolder extends ViewHolderBase<Address> {

        @BindView(R.id.tv_address_list_name)
        TextView tvAddressListName;
        @BindView(R.id.tv_address_list_address)
        TextView tvAddressListAddress;
        @BindView(R.id.tv_address_list_default)
        TextView tvAddressListDefault;
        @BindView(R.id.tv_address_list_delete)
        TextView tvAddressListDelete;
        @BindView(R.id.tv_address_list_edit)
        TextView tvAddressListEdit;

        private Address address;

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item_address, parent, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void showData(int position, Address address) {
            this.address = address;
            tvAddressListName.setText(String.format("%s  %s", address.recipientName, address.recipientPhone));
            tvAddressListAddress.setText(String.format("%s%s%s%s", address.province, address.city, address.county, address.address));
            tvAddressListDefault.setCompoundDrawablesWithIntrinsicBounds(address.isDefault ? R.drawable.ic_check_box_on : R.drawable.ic_check_box_off, 0, 0, 0);
        }

        @OnClick({R.id.tv_address_list_default, R.id.tv_address_list_delete, R.id.tv_address_list_edit})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_address_list_default:
                    showLoadingDialog();
                    Api.addressDefault(address.id, new JsonResultCallback<JsonResultVoid>() {
                        @Override
                        public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                            dismissLoadingDialog();
                            setAddressDefault(address.id);
                        }

                        @Override
                        public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                            super.onFailure(statusCode, responseString, throwable, tag);
                            dismissLoadingDialog();
                            UI.showToast(responseString);
                        }
                    });
                    break;
                case R.id.tv_address_list_delete:
                    UI.showConfirmDialog(AddressListActivity.this, "温馨提示","确定删除该地址?", "取消", null, "删除", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showLoadingDialog();
                            Api.addressDelete(address.id, new JsonResultCallback<JsonResultVoid>() {
                                @Override
                                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                                    dismissLoadingDialog();
                                    deleteAddress(address.id);
                                }

                                @Override
                                public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                                    super.onFailure(statusCode, responseString, throwable, tag);
                                    dismissLoadingDialog();
                                    UI.showToast(responseString);
                                }
                            });
                        }
                    });
                    break;
                case R.id.tv_address_list_edit:
                    AddressAddActivity.start(AddressListActivity.this, address);
                    break;
            }
        }
    }
}
