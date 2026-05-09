package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 文章实体类
 * 知识点:
 *   1. JPA 实体映射 - @Entity, @Id, @GeneratedValue
 *   2. 字段映射 - @Column
 *   3. 枚举映射 - @Enumerated
 *   4. 自动填充时间 - @CreationTimestamp, @UpdateTimestamp
 *   5. Lombok 简化代码
 */
@Entity
@Table(name = "articles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String author;

    /** 文章状态: DRAFT-草稿, PUBLISHED-已发布 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ArticleStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ArticleStatus {
        DRAFT, PUBLISHED
    }
}
