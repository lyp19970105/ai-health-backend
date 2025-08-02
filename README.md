# Health Monitoring Backend

## 1. 项目概述 (Project Overview)

本项目是 **健康监测应用** 的后端服务，定位为连接前端用户与第三方 AI 服务的 **API 网关** 和 **业务处理中心**。

其核心作用是：
- 为前端应用提供一套统一、安全的 RESTful API。
- 管理用户认证、授权和会话。
- 封装对底层第三方 AI 服务（如 SiliconFlow, Dify）的调用逻辑。
- 处理核心业务逻辑，如保存和管理聊天记录。

## 2. 技术栈 (Technology Stack)

- **核心框架**: Spring Boot 3.3.1
- **编程语言**: Java 17
- **构建工具**: Apache Maven
- **数据库 ORM**: Spring Data JPA / Hibernate
- **数据库**: MySQL
- **Web 技术**:
  - **Spring MVC**: 用于处理标准的 REST API 请求。
  - **Spring WebFlux**: 用于实现与 AI 聊天的流式响应 (Server-Sent Events)，以支持前端的打字机效果。
- **安全框架**:
  - **Spring Security**: 负责用户认证和接口授权。
  - **Spring Session JDBC**: 将用户会话信息持久化到数据库，实现分布式会话管理。
- **API 文档**: SpringDoc (OpenAPI 3 / Swagger UI)
- **JSON 库**: FastJSON2
- **辅助工具**: Lombok

## 3. 项目架构 (Project Architecture)

项目采用经典的分层架构，确保代码的模块化和可维护性。

```
health-monitoring/
└── src/
    └── main/
        └── java/
            └── com/example/healthmonitoring/
                ├── client/         # 外部服务客户端 (调用 Dify, SiliconFlow)
                ├── config/         # 应用配置 (Web, 安全, API文档等)
                ├── controller/     # API 入口层 (处理 HTTP 请求)
                ├── dto/            # 数据传输对象 (定义 API 请求和响应体)
                ├── exception/      # 全局异常处理
                ├── model/          # 数据模型/实体类
                ├── repository/     # 数据访问层 (JPA Repositories)
                ├── security/       # Spring Security 相关实现
                └── service/        # 核心业务逻辑层
```

- **`controller`**: 接收来自客户端的 HTTP 请求，进行参数校验，并调用 `service` 层处理业务。
- **`service`**: 实现应用的核心业务逻辑。它会编排 `repository` 和 `client` 来完成一个完整的业务流程。
- **`repository`**: 数据访问层，通过 Spring Data JPA 与数据库进行交互。
- **`client`**: 封装了对第三方 API (Dify, SiliconFlow) 的调用逻辑，使得业务层可以像调用本地方法一样调用外部服务。
- **`dto`**: 定义了 API 的数据结构，是各层之间数据交换的载体。
- **`security`**: 包含用户身份验证（`UserDetailsService`）和会话管理的核心实现。

## 4. 主要功能模块 (Main Features)

- **用户认证**: 提供标准的注册 (`/register`)、登录 (`/login`) 和获取当前用户信息 (`/me`) 的接口。
- **文本聊天**: 支持与 AI 进行纯文本的流式对话。响应通过 Server-Sent Events (SSE) 推送，以实现实时的打字机效果。
- **多模态聊天 (VLM)**: 支持图文对话。该功能非常灵活，允许用户通过以下两种方式提供图片：
  1.  **直接上传图片文件**
  2.  **提供图片的公开 URL**
- **会话管理**: (推断) 具备记录和查询用户历史聊天记录的功能，方便用户回顾过往对话。

## 5. 核心 API 端点 (Core API Endpoints)

所有 API 均以 `/api` 为前缀。

- `POST /api/auth/login`: 用户登录。
- `POST /api/auth/register`: 注册新用户。
- `GET /api/auth/me`: 获取当前已登录用户的信息。
- `POST /api/chat/stream`: **[流式]** 发起纯文本聊天请求。
- `POST /api/chat/vlm/stream`: **[流式]** 发起多模态（图文）聊天请求。

## 6. 配置说明 (Configuration)

- **主配置文件**: `src/main/resources/application.properties`
- **关键配置项**:
  - `spring.datasource.*`: 数据库连接信息。
  - `spring.session.*`: Spring Session 的配置，用于会话管理。
  - `spring.servlet.multipart.*`: 文件上传大小限制。
  - `siliconflow.api.*`: SiliconFlow 服务的 API 地址和密钥。
  - `dify.api.url`: Dify 服务的 API 地址。

> **⚠️ 安全警告**
>
> 当前版本中，`siliconflow.api.key` 等敏感信息被**硬编码**在 `application.properties` 文件中。这在开发环境中是可接受的，但在生产环境中存在严重的安全风险。
>
> **强烈建议**在部署到生产环境时，使用**环境变量**、**Docker Secrets** 或 **配置中心** (如 Nacos, Spring Cloud Config) 来管理这些敏感信息。

## 7. 如何运行 (How to Run)

1.  **环境准备**:
    - 确保已安装 Java 17 (or newer) 和 Maven。
    - 确保本地或网络上有一个正在运行的 MySQL 8.x 实例。
    - 在 MySQL 中创建一个名为 `ai_app` 的数据库: `CREATE DATABASE ai_app;`

2.  **配置修改**:
    - 打开 `src/main/resources/application.properties`。
    - 根据你的环境，修改 `spring.datasource.url`, `spring.datasource.username`, 和 `spring.datasource.password`。
    - (可选) 修改 `siliconflow.api.key` 和 `dify.api.url` 为你自己的密钥和地址。

3.  **启动应用**:
    - **通过 Maven 命令行**:
      ```bash
      mvn spring-boot:run
      ```
    - **通过 IDE**:
      - 直接找到 `HealthMonitoringApplication.java` 文件。
      - 右键点击并选择 "Run" 或 "Debug"。

4.  **访问 API 文档**:
    - 应用启动后，在浏览器中访问以下地址即可查看所有 API 的详细文档和进行在线测试：
      [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)