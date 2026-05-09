package com.example.blog.service;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.PageResult;
import com.example.blog.entity.Article;
import com.example.blog.entity.Article.ArticleStatus;
import com.example.blog.exception.BizException;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文章业务层（增强版）
 * 新增知识点:
 *   1. 分页查询 - Pageable, Page, PageRequest
 *   2. Redis 缓存 - @Cacheable, @CacheEvict
 *   3. 缓存策略: 读缓存 → 没有 → 查库 → 写缓存
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    // ====== 分页查询已发布文章 ======

    /**
     * 分页查询已发布文章
     * 知识点:
     *   1. @Cacheable - 方法结果会被缓存到 Redis
     *   2. key = "articles:published:{page}:{size}" - 缓存 Key
     *   3. 第一次查询会执行 SQL，后续直接从 Redis 取
     *   4. cacheManager 指定使用我们配置的 Redis 缓存管理器
     */
    @Cacheable(value = "articles", key = "'published:' + #page + ':' + #size")
    public PageResult<ArticleDTO> listPublished(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Article> articlePage = articleRepository.findByStatus(ArticleStatus.PUBLISHED, pageable);
        return toPageResult(articlePage);
    }

    // ====== 分页查询所有文章 ======

    public PageResult<ArticleDTO> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Article> articlePage = articleRepository.findAll(pageable);
        return toPageResult(articlePage);
    }

    // ====== 查询单篇（带缓存） ======

    @Cacheable(value = "article", key = "#id")
    public ArticleDTO getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BizException(404, "文章不存在: " + id));
        return toDTO(article);
    }

    // ====== 创建文章（清除缓存） ======

    /**
     * 知识点: @CacheEvict - 创建/修改/删除时清除缓存，保证数据一致性
     * allEntries = true - 清除该缓存名下的所有条目
     */
    @Transactional
    @CacheEvict(value = {"articles", "article"}, allEntries = true)
    public ArticleDTO create(ArticleDTO dto) {
        Article article = Article.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(dto.getAuthor() != null ? dto.getAuthor() : "匿名")
                .status(dto.getStatus() != null ? dto.getStatus() : ArticleStatus.DRAFT)
                .build();
        article = articleRepository.save(article);
        return toDTO(article);
    }

    // ====== 更新文章（清除缓存） ======

    @Transactional
    @CacheEvict(value = {"articles", "article"}, allEntries = true)
    public ArticleDTO update(Long id, ArticleDTO dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BizException(404, "文章不存在: " + id));

        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        if (dto.getAuthor() != null) {
            article.setAuthor(dto.getAuthor());
        }
        if (dto.getStatus() != null) {
            article.setStatus(dto.getStatus());
        }
        article = articleRepository.save(article);
        return toDTO(article);
    }

    // ====== 删除文章（清除缓存） ======

    @Transactional
    @CacheEvict(value = {"articles", "article"}, allEntries = true)
    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new BizException(404, "文章不存在: " + id);
        }
        articleRepository.deleteById(id);
    }

    // ====== 搜索（分页） ======

    public PageResult<ArticleDTO> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepository.findByTitleContaining(keyword, pageable);
        return toPageResult(articlePage);
    }

    // ====== 工具方法 ======

    private PageResult<ArticleDTO> toPageResult(Page<Article> page) {
        return PageResult.from(page.map(this::toDTO));
    }

    private ArticleDTO toDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setAuthor(article.getAuthor());
        dto.setStatus(article.getStatus());
        dto.setCommentCount((int) commentRepository.countByArticleId(article.getId()));
        dto.setCreatedAt(article.getCreatedAt() != null ? article.getCreatedAt().toString() : null);
        dto.setUpdatedAt(article.getUpdatedAt() != null ? article.getUpdatedAt().toString() : null);
        return dto;
    }
}
