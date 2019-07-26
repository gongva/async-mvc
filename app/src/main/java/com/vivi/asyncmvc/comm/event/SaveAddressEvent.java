package com.vivi.asyncmvc.comm.event;

import com.vivi.asyncmvc.api.entity.Address;
import com.vivi.asyncmvc.library.plugs.otto.Event;

/**
 * 新增/编辑收货地址成功
 */
public class SaveAddressEvent extends Event {
    public Address address;

    public SaveAddressEvent(Address address) {
        this.address = address;
    }
}
