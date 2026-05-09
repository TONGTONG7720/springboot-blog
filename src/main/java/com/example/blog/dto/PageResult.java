package com.example.blog.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应封装
 * 知识点:
 *   1. Spring Data 的 Page 对象包含丰富的分页信息
 *   2. 自定义封装避免直接暴露 Spring 内部对象
 */
@Data
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> content;

    /** 当前页码（从0开始） */
    private int page;

    /** 每页大小 */
    private int size;

    /** 总元素数 */
    private long totalElements;

    /** 总页数 */
    private int totalPages;

    /** 是否第一页 */
    private boolean first;

    /** 是否最后一页 */
    private boolean last;

    /**
     * 从 Spring Data Page 转换
     */
    public static <T> PageResult<T> from(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setContent(page.getContent());
        result.setPage(page.getNumber());
        result.setSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setFirst(page.isFirst());
        result.setLast(page.isLast());
        return result;
    }
}
