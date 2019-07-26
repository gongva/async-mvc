package com.vivi.asyncmvc.api.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统字典数据
 *
 * @author gongwei
 * @date 2019/2/12
 */
public class Dict {
    public String code;
    public String name;

    public Dict(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * List<Dict> 的name转String[]
     *
     * @param dictList
     * @return
     */
    public static String[] getNameArray(List<Dict> dictList) {
        String[] result = new String[dictList.size()];
        if (dictList != null) {
            for (int i = 0; i < dictList.size(); i++) {
                result[i] = dictList.get(i).name;
            }
        }
        return result;
    }

    /**
     * List<Dict> 的name转List<String>
     *
     * @param dictList
     * @return
     */
    public static List<String> getNameList(List<Dict> dictList) {
        List<String> result = new ArrayList<>();
        if (dictList != null) {
            for (int i = 0; i < dictList.size(); i++) {
                result.add(dictList.get(i).name);
            }
        }
        return result;
    }
}
