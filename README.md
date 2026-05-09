# Spring Boot 学习项目 - 个人博客系统

一个适合 Spring Boot 初学者的实战项目，覆盖核心知识点。

## 项目结构

```
src/main/java/com/example/blog/
├── BlogApplication.java          # 启动类
├── entity/                        # 实体类
│   ├── Article.java              # 文章
│   ├── Comment.java              # 评论
│   └── User.java                 # 用户（实现 UserDetails）
├── repository/                    # 数据访问层
├── service/                       # 业务逻辑层
│   ├── ArticleService.java       # 文章（分页+缓存）
│   ├── CommentService.java       # 评论
│   ├── AuthService.java          # 认证（注册+登录）
│   └── CustomUserDetailsService.java
├── controller/                    # 控制器层
│   ├── ArticleController.java
│   ├── CommentController.java
│   └── AuthController.java
├── dto/                           # 数据传输对象
├── security/                      # 安全模块
│   ├── SecurityConfig.java       # Spring Security 配置
│   ├── JwtUtils.java             # JWT 工具类
│   └── JwtAuthFilter.java        # JWT 认证过滤器
├── config/                        # 配置
│   ├── WebConfig.java            # CORS
│   ├── RedisConfig.java          # Redis 缓存配置
│   └── DataInitializer.java      # 初始数据
└── exception/                     # 异常处理
```

## 技术栈

| 组件 | 技术 |
|------|------|
| 框架 | Spring Boot 3.2 |
| ORM | Spring Data JPA |
| 数据库 | H2（内存数据库） |
| 安全 | Spring Security + JWT |
| 缓存 | Redis |
| 校验 | Jakarta Validation |
| 工具 | Lombok |
| 测试 | JUnit 5 + MockMvc |

## 快速启动

### 方式一：有 Redis（完整体验）

```bash
# 先启动 Redis
redis-server

# 启动项目
mvn spring-boot:run
```

### 方式二：没有 Redis（跳过缓存）

修改 `application.yml`，注释掉 Redis 和缓存配置，或改用内存缓存：

```yaml
spring:
  cache:
    type: simple  # 用内存缓存替代 Redis
```

### 启动后访问

- API: http://localhost:8080/api/articles
- H2 控制台: http://localhost:8080/h2-console

## API 接口

### 🔓 公开接口（无需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 注册 |
| POST | /api/auth/login | 登录，返回 JWT Token |
| GET | /api/articles?page=0&size=10 | 分页查询文章 |
| GET | /api/articles/{id} | 查询单篇文章 |
| GET | /api/articles/search?keyword=xxx | 搜索文章 |
| GET | /api/articles/{id}/comments | 获取文章评论 |

### 🔒 需要登录（Header 加 Authorization: Bearer {token}）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/auth/me | 获取当前用户 |
| POST | /api/articles | 创建文章 |
| PUT | /api/articles/{id} | 更新文章 |
| DELETE | /api/articles/{id} | 删除文章 |
| POST | /api/articles/{id}/comments | 添加评论 |

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| user | 123456 | 普通用户 |

## 快速测试

```bash
# 1. 注册新用户
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","nickname":"测试用户"}'

# 2. 登录（拿到 Token）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 3. 用 Token 创建文章
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer 你的Token" \
  -d '{"title":"我的第一篇文章","content":"Hello World!","status":"PUBLISHED"}'

# 4. 分页查询
curl http://localhost:8080/api/articles?page=0&size=5
```

## 学习路线

### 第一阶段：基础 CRUD
1. 跑通项目，调用 API
2. 理解分层架构
3. 读懂实体映射和数据访问

### 第二阶段：安全认证
1. 理解 Spring Security 过滤器链
2. JWT Token 的生成和验证流程
3. 公开接口 vs 受保护接口

### 第三阶段：缓存优化
1. Redis 基本操作
2. @Cacheable 缓存读取流程
3. @CacheEvict 缓存清除策略

### 第四阶段：动手扩展
- [ ] 实现文章分类/标签
- [ ] 添加文件上传（文章配图）
- [ ] 实现点赞/收藏功能
- [ ] 加入 Swagger 接口文档
- [ ] 把 H2 换成 MySQL
- [ ] 添加操作日志（AOP）
- [ ] 实现接口限流（Redis + 注解）

## 知识点清单

✅ 分层架构（Controller → Service → Repository）
✅ JPA 实体映射与数据库操作
✅ RESTful API 设计
✅ DTO 与统一响应封装
✅ 参数校验（@Valid）
✅ 全局异常处理（@RestControllerAdvice）
✅ Spring Security 安全框架
✅ JWT 无状态认证
✅ Redis 缓存（@Cacheable / @CacheEvict）
✅ 分页查询（Pageable / Page）
✅ CORS 跨域配置
✅ 密码加密（BCrypt）
✅ 自定义 UserDetailsService
✅ CommandLineRunner 初始化数据
✅ 单元测试（MockMvc）
