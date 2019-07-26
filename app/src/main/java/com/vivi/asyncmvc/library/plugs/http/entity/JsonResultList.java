package com.vivi.asyncmvc.library.plugs.http.entity;

import java.util.List;

/**
 * 获取返回数据列表[带页数分页]
 *
 * @author gongwei
 * @created 2018/12/19
 */
public class JsonResultList<T> extends JsonResultVoid {

    public PageList<T> data;

    public boolean hasNextPage() {
        return data.page < data.totalPage;
    }

    public int getTotalPage() {
        return data.totalPage;
    }

    public int getTotalCount() {
        return data.totalCount;
    }

    public int getPageSize() {
        return data.size;
    }

    public int getCurrentPage() {
        return data.page;
    }

    public List<T> getData() {
        return data.list;
    }

    public static <T> JsonResultList<T> createJsonResult(List<T> data, boolean success) {
        JsonResultList<T> jsonResult = new JsonResultList<>();
        jsonResult.success = success;
        PageList<T> pageList = new PageList<>();
        pageList.list = data;
        jsonResult.data = pageList;
        return jsonResult;
    }

    public static class PageList<T> {
        public int totalPage;
        public int totalCount;
        public int size;
        public int page;
        public List<T> list;

    }
}
