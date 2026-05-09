package com.example.blog.config;

import com.example.blog.entity.Article;
import com.example.blog.entity.Article.ArticleStatus;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 初始数据加载（增强版）
 * 新增: 预置一个测试用户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ====== 创建测试用户 ======
        User user = userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .nickname("管理员")
                .role("ROLE_ADMIN")
                .build());

        User normalUser = userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("123456"))
                .nickname("普通用户")
                .role("ROLE_USER")
                .build());

        log.info("✅ 测试用户已创建: admin/123456, user/123456");

        // ====== 创建示例文章 ======
        Article article1 = articleRepository.save(Article.builder()
                .title("Spring Boot 入门指南")
                .content("Spring Boot 让 Java Web 开发变得简单。它自动配置了 Spring 应用，你只需要关注业务逻辑。\n\n" +
                        "## 快速开始\n" +
                        "1. 创建项目\n" +
                        "2. 添加依赖\n" +
                        "3. 写一个 Hello World\n" +
                        "4. 运行！")
                .author("管理员")
                .status(ArticleStatus.PUBLISHED)
                .build());

        Article article2 = articleRepository.save(Article.builder()
                .title("RESTful API 设计规范")
                .content("好的 API 设计让前后端协作更顺畅。\n\n" +
                        "## 核心原则\n" +
                        "- 使用名词而非动词\n" +
                        "- HTTP 方法表示操作\n" +
                        "- 合理使用状态码\n" +
                        "- 统一响应格式")
                .author("管理员")
                .status(ArticleStatus.PUBLISHED)
                .build());

        Article article3 = articleRepository.save(Article.builder()
                .title("JPA vs MyBatis 怎么选")
                .content("这是一篇草稿，还在写...")
                .author("管理员")
                .status(ArticleStatus.DRAFT)
                .build());

        // ====== 创建评论 ======
        commentRepository.save(Comment.builder()
                .nickname("小明")
                .content("写得真清楚，感谢分享！")
                .article(article1)
                .build());

        commentRepository.save(Comment.builder()
                .nickname("小红")
                .content("正好在学 Spring Boot，太及时了")
                .article(article1)
                .build());

        commentRepository.save(Comment.builder()
                .nickname("路人甲")
                .content("RESTful 这块讲得好，收藏了")
                .article(article2)
                .build());

        log.info("✅ 初始化数据完成: 2个用户, 3篇文章, 3条评论");
    }
}
