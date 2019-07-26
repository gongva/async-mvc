package com.vivi.asyncmvc.ui.comm.web;

import android.studio.util.StringUtils;
import android.text.TextUtils;

import com.vivi.asyncmvc.library.utils.CommonTools;

import java.util.HashSet;

/**
 * Web协议指令集合
 * <p>
 * Author: gongwei 2019.1.2
 */
public class ActionProtocol {

    //协议前缀 eg:http://web.hikcreate.com/ActivityProtocal/call?phone=13812345678
    public static final String PROTOCOL_HEADER = "http://web.hikcreate.com/ActivityProtocal/";

    //拨打电话?phone=电话号码
    public static final String CALL = "call";

    /**
     * 协议参数
     */
    public static class ProtocolParam {
        public String name;
        public String value;
    }

    /**
     * 解析协议
     *
     * @param actionUrl
     * @return
     */
    public static ProtocolEntity decodeProtocol(String actionUrl) {
        if (TextUtils.isEmpty(actionUrl) || !StringUtils.startsWithIgnoreCase(actionUrl, PROTOCOL_HEADER)) {
            return null;
        } else {
            ProtocolEntity protocolEntity = new ProtocolEntity();
            String[] strs = actionUrl.substring(PROTOCOL_HEADER.length()).split("\\?");
            protocolEntity.setName(strs[0]);
            if (strs.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < strs.length; i++) {
                    if (i > 1) {
                        sb.append("?");
                    }
                    sb.append(strs[i]);
                }
                protocolEntity.setParams(sb.toString());
            }
            return protocolEntity;
        }
    }

    /**
     * 协议内容
     */
    public static class ProtocolEntity {

        //协议名
        private String name;

        //协议参数字符串
        private HashSet<ProtocolParam> paramSet;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取参数值，忽略大小写, 没有返回空串
         *
         * @param key
         * @return
         */
        public String getParam(String key) {
            if (paramSet.size() > 0) {
                for (ProtocolParam param : paramSet) {
                    if (param.name != null && param.name.equalsIgnoreCase(key)) {
                        return param.value;
                    }
                }
            }
            return "";
        }

        public void setParams(String params) {
            this.paramSet = decodeParams(params);
        }


        /**
         * 解析协议参数
         *
         * @return
         */
        private HashSet<ProtocolParam> decodeParams(String paramStr) {
            HashSet<ProtocolParam> hashSetParam = new HashSet<>();
            if (TextUtils.isEmpty(paramStr)) return hashSetParam;
            String[] shareQuery = paramStr.split("&");
            for (String s : shareQuery) {
                if (TextUtils.isEmpty(s)) continue;
                int indexOfSp = s.indexOf("=");
                if (indexOfSp != -1) {
                    ProtocolParam protocolParams = new ProtocolParam();
                    protocolParams.name = s.substring(0, indexOfSp);
                    protocolParams.value = CommonTools.decodeUrl(indexOfSp + 1 < s.length() ? s.substring(indexOfSp + 1) : "");
                    hashSetParam.add(protocolParams);
                }
            }
            return hashSetParam;
        }
    }

}
