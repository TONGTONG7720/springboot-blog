package com.example.blog.repository;

import com.example.blog.entity.Article;
import com.example.blog.entity.Article.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章数据访问层（增强版）
 * 新增: 分页查询方法 - 返回 Page<T>
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // ====== 分页查询 ======

    /** 按状态分页查询 - 返回 Page 对象 */
    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);

    /** 按标题模糊搜索 - 分页 */
    Page<Article> findByTitleContaining(String keyword, Pageable pageable);

    // ====== 列表查询（保留，供非分页场景使用） ======

    List<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status);

    List<Article> findByTitleContaining(String keyword);

    @Query("SELECT a FROM Article a WHERE a.status = :status ORDER BY a.createdAt DESC")
    List<Article> findPublishedArticles(@Param("status") ArticleStatus status);
}
