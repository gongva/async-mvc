package com.vivi.asyncmvc.api.entity.article;

import java.util.List;

/**
 * 安全行驶-一级文章分类
 *
 * @author gongwei
 * @date 2019/2/18
 */
public class ArticleCategory {
    public String type;
    public String name;
    public List<Article> items;
}