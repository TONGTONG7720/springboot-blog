package com.example.blog.controller;

import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.Result;
import com.example.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 * RESTful 嵌套资源: /api/articles/{articleId}/comments
 */
@RestController
@RequestMapping("/api/articles/{articleId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * GET /api/articles/{articleId}/comments - 获取文章评论
     */
    @GetMapping
    public Result<List<CommentDTO>> list(@PathVariable Long articleId) {
        return Result.success(commentService.listByArticleId(articleId));
    }

    /**
     * POST /api/articles/{articleId}/comments - 添加评论
     */
    @PostMapping
    public Result<CommentDTO> create(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentDTO dto) {
        return Result.success(commentService.create(articleId, dto));
    }

    /**
     * DELETE /api/comments/{commentId} - 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return Result.success();
    }
}
