package com.example.blog.dto;

import com.example.blog.entity.Article.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文章数据传输对象
 * 知识点:
 *   1. DTO vs Entity - 为什么需要 DTO？(解耦、安全、灵活)
 *   2. 参数校验 - @NotBlank, @Size
 *   3. 请求和响应分离
 */
@Data
public class ArticleDTO {

    /** 创建/更新文章时的请求体 */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多200个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String author;

    private ArticleStatus status;

    /** 文章列表响应 */
    private Long id;
    private Integer commentCount;
    private String createdAt;
    private String updatedAt;
}
