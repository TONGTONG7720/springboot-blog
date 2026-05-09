package com.example.blog.controller;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.PageResult;
import com.example.blog.dto.Result;
import com.example.blog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器（增强版）
 * 新增知识点:
 *   1. 分页参数 - @RequestParam page, size
 *   2. 参数默认值 - defaultValue
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * GET /api/articles?page=0&size=10&status=published
     * 分页查询文章列表
     */
    @GetMapping
    public Result<PageResult<ArticleDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        PageResult<ArticleDTO> result = "published".equals(status)
                ? articleService.listPublished(page, size)
                : articleService.listAll(page, size);
        return Result.success(result);
    }

    /**
     * GET /api/articles/{id}
     */
    @GetMapping("/{id}")
    public Result<ArticleDTO> getById(@PathVariable Long id) {
        return Result.success(articleService.getById(id));
    }

    /**
     * POST /api/articles（需要登录）
     */
    @PostMapping
    public Result<ArticleDTO> create(@Valid @RequestBody ArticleDTO dto) {
        return Result.success(articleService.create(dto));
    }

    /**
     * PUT /api/articles/{id}（需要登录）
     */
    @PutMapping("/{id}")
    public Result<ArticleDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ArticleDTO dto) {
        return Result.success(articleService.update(id, dto));
    }

    /**
     * DELETE /api/articles/{id}（需要登录）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }

    /**
     * GET /api/articles/search?keyword=xxx&page=0&size=10
     * 搜索文章（分页）
     */
    @GetMapping("/search")
    public Result<PageResult<ArticleDTO>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(articleService.search(keyword, page, size));
    }
}
