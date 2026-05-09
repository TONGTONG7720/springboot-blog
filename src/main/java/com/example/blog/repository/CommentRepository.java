package com.example.blog.repository;

import com.example.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论数据访问层
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** 根据文章ID查评论，按时间倒序 */
    List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);

    /** 统计某篇文章的评论数 */
    long countByArticleId(Long articleId);
}
