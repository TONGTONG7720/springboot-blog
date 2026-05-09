package com.example.blog.service;

import com.example.blog.dto.CommentDTO;
import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.exception.BizException;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论业务层
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    /**
     * 获取某篇文章的所有评论
     */
    public List<CommentDTO> listByArticleId(Long articleId) {
        // 先检查文章是否存在
        if (!articleRepository.existsById(articleId)) {
            throw new BizException(404, "文章不存在: " + articleId);
        }
        return commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 添加评论
     */
    @Transactional
    public CommentDTO create(Long articleId, CommentDTO dto) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BizException(404, "文章不存在: " + articleId));

        Comment comment = Comment.builder()
                .nickname(dto.getNickname())
                .content(dto.getContent())
                .article(article)
                .build();
        comment = commentRepository.save(comment);
        return toDTO(comment);
    }

    /**
     * 删除评论
     */
    @Transactional
    public void delete(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new BizException(404, "评论不存在: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    private CommentDTO toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setNickname(comment.getNickname());
        dto.setContent(comment.getContent());
        dto.setArticleId(comment.getArticle().getId());
        dto.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
        return dto;
    }
}
