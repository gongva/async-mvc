package com.vivi.asyncmvc.library.plugs.http.entity;

import java.util.List;

/**
 * 获取返回数据列表[带时间戳分页]
 *
 * @author gongwei
 * @created 2018/12/19
 */
public class JsonResultTsList<T> extends JsonResultVoid {

    public boolean hasNextPage() {
        return data.list.size() == data.size;
    }

    private PageList<T> data;

    public int getTotalCount() {
        return data.totalCount;
    }

    public int getPageSize() {
        return data.size;
    }

    public long getTs() {
        return data.timestamp;
    }

    public List<T> getData() {
        return data.list;
    }

    public List<T> getDataList() {
        return data.list;
    }

    class PageList<T> {
        private long timestamp;
        private int totalCount;
        private int size;
        private List<T> list;
    }
}
