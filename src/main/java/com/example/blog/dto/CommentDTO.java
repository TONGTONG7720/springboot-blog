package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论数据传输对象
 */
@Data
public class CommentDTO {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称最多50个字符")
    private String nickname;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容最多1000个字符")
    private String content;

    // 响应字段
    private Long id;
    private Long articleId;
    private String createdAt;
}
