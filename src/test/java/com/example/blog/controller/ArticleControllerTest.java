package com.example.blog.controller;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.entity.Article.ArticleStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 文章控制器测试
 * 知识点:
 *   1. @SpringBootTest - 启动完整 Spring 容器
 *   2. @AutoConfigureMockMvc - 自动配置 MockMvc
 *   3. MockMvc - 模拟 HTTP 请求测试 Controller
 *   4. JSON Path - 验证响应 JSON 结构
 */
@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListArticles() throws Exception {
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldCreateArticle() throws Exception {
        ArticleDTO dto = new ArticleDTO();
        dto.setTitle("测试文章");
        dto.setContent("这是一篇测试文章的内容");
        dto.setAuthor("测试用户");
        dto.setStatus(ArticleStatus.PUBLISHED);

        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("测试文章"));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        ArticleDTO dto = new ArticleDTO();
        dto.setTitle("");  // 空标题，应该触发校验
        dto.setContent("内容");

        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
