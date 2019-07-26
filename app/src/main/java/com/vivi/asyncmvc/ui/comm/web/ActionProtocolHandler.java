package com.vivi.asyncmvc.ui.comm.web;

import android.app.Activity;
import android.content.Context;
import android.studio.util.StringUtils;
import android.text.TextUtils;

import com.vivi.asyncmvc.library.utils.UI;

/**
 * Desc: Web指令协议处理
 *
 * @author gongwei  2019.1.2
 */
public class ActionProtocolHandler {

    protected static final String UNSUPPORTED_ORDER = "请升级到最新版后查看";

    public static ActionProtocolHandler INSTANCE;

    public static ActionProtocolHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (ActionProtocolHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActionProtocolHandler();
                }
            }
        }
        return INSTANCE;
    }

    public boolean handleActionProtocol(Context context, String actionUrl) {
        if (TextUtils.isEmpty(actionUrl)) return false;
        if (StringUtils.startsWithIgnoreCase(actionUrl, ActionProtocol.PROTOCOL_HEADER)) {
            ActionProtocol.ProtocolEntity protocolEntity = ActionProtocol.decodeProtocol(actionUrl);
            if (protocolEntity.getName().equalsIgnoreCase(ActionProtocol.CALL)) {
                callPhone(context, protocolEntity.getParam("phone"));
            } else {
                //不能识别的指令类型
                UI.showToast(UNSUPPORTED_ORDER);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打电话
     * @param context
     * @param phone
     */
    public void callPhone(Context context, String phone) {
        if (context!= null && context instanceof Activity) {
            UI.makeCall((Activity) context, phone);
        }
    }
}
