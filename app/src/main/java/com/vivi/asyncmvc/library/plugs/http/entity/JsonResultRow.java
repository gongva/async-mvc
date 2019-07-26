package com.vivi.asyncmvc.library.plugs.http.entity;

import java.util.List;

/**
 * 用于数据是列表[无分页]
 *
 * @author gongwei
 * @created 2018/12/19
 */
public class JsonResultRow<T> extends JsonResultVoid {

    private List<T> data;
    private int rows;
    @Deprecated
    public int PraiseRate;

    public List<T> getData() {
        return data;
    }

    public boolean isNextPage(int pageIndex) {
        return pageIndex < rows;
    }
}
