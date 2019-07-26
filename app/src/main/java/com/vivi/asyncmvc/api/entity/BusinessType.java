package com.vivi.asyncmvc.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 温馨服务-问题类型
 *
 * @author gongwei
 * @date 2019/2/13
 */
public class BusinessType {
    public String businessTypeCode;// 业务类型编码
    public String businessTypeName;// 业务类型名称
    public List<QuestionType> questionTypes;

    /**
     * List<BusinessType> 的businessTypeName转List<String>
     *
     * @param list
     * @return
     */
    public static List<String> getNameList(List<BusinessType> list) {
        List<String> result = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                result.add(list.get(i).businessTypeName);
            }
        }
        return result;
    }

    public class QuestionType implements Serializable {
        public String questionTypeCode;//问题类型编码
        public String questionTypeName;//问题类型名称
    }
}
