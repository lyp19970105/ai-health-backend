# 健康监测 (Health Monitoring) 后端服务

这是一个基于 Spring Boot 的Java后端服务，为健康监测应用提供API接口。它支持多种大语言模型平台，并记录所有对话历史，方便用户回顾和分析。

## 1. 主要功能

- **多平台支持**: 可通过配置，轻松切换和使用不同的大语言模型平台（如 Dify, SiliconFlow）。
- **健康检查**: 提供一个 `/api/health` 端点，用于检查服务的运行状态。
- **实时聊天与连续对话**: 基于 SSE (Server-Sent Events) 实现流式聊天，并支持上下文关联的连续对话。
- **对话历史**: 自动记录所有用户与AI的对话，并提供查询接口。

## 2. 技术栈

- **Java 17**: 项目使用的编程语言。
- **Spring Boot 3.3.1**: 核心框架，用于快速构建独立的、生产级的Spring应用程序。
- **Spring Web**: 用于构建Web应用，包括RESTful API。
- **Spring Data JPA**: 用于简化数据访问层（DAO）的实现。
- **MySQL Connector/J**: MySQL数据库的JDBC驱动。
- **Maven**: 用于项目构建和依赖管理。

## 3. API 文档

### 3.1 应用管理 (App Management)

#### **GET /api/v1/apps**

- **描述**: 获取所有已配置的应用列表。
- **请求参数**: 无
- **成功响应 (200 OK)**:
  ```json
  [
    {
      "appCode": "health_qa",
      "appName": "健康问答",
      "modelName": "gpt-3.5-turbo",
      "platform": "DIFY",
      "createdAt": "2025-07-19T10:00:00",
      "updatedAt": "2025-07-19T10:00:00"
    }
  ]
  ```

### 3.2 实时聊天 (Real-time Chat)

#### **POST /api/chat**

- **描述**: 发送聊天消息，并通过 SSE 流式获取回复。支持通过 `conversationId` 实现连续对话。
- **请求体**:
  ```json
  {
    "appCode": "health_qa",
    "userInput": "你好，我最近总感觉很累，是怎么回事？",
    "conversationId": 123 // 可选，如果是已存在的对话，请传入此ID
  }
  ```
- **成功响应 (SSE Stream)**:
  - 服务端会以 `text/event-stream` 的形式持续推送事件。
  - **`conversation_id` 事件**: 在流的开始，会首先收到此事件，前端应保存该ID，用于后续的连续对话请求。
    ```
    event: conversation_id
    data: 124
    ```
  - **`message` 事件**: 前端可以通过监听此事件来接收AI的回复片段。
    ```
    event: message
    data: 您好，疲劳是很常见的问题...
    ```

### 3.3 对话历史 (Conversation History)

#### **GET /api/v1/conversations**

- **描述**: 分页获取指定应用的对话列表。
- **请求参数**:
  - `appCode` (string, 必需): 应用的唯一标识符 (例如，`health_qa`)。
  - `page` (integer, 可选, 默认 0): 页码。
  - `size` (integer, 可选, 默认 20): 每页数量。
- **成功响应 (200 OK)**:
  ```json
  {
    "content": [
      {
        "id": 123,
        "name": "Conversation with health_qa",
        "createdAt": "2025-07-19T10:05:00",
        "updatedAt": "2025-07-19T10:05:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      // ... more pagination details
    },
    "totalElements": 1,
    "totalPages": 1
  }
  ```

#### **GET /api/v1/conversations/{conversationId}**

- **描述**: 获取指定对话的详细信息，包括所有消息。
- **路径参数**:
  - `conversationId` (long, 必需): 对话的唯一ID。
- **成功响应 (200 OK)**:
  ```json
  {
    "id": 123,
    "name": "Conversation with health_qa",
    "createdAt": "2025-07-19T10:05:00",
    "updatedAt": "2025-07-19T10:05:00",
    "messages": [
      {
        "id": 1,
        "role": "user",
        "content": "你好，我最近总感觉很累，是怎么回事？",
        "createdAt": "2025-07-19T10:05:01"
      },
      {
        "id": 2,
        "role": "assistant",
        "content": "您好，疲劳是很常见的问题，可能由多种原因导致...",
        "createdAt": "2025-07-19T10:05:05"
      }
    ]
  }
  ```

### 3.4 健康检查 (Health Check)

#### **GET /api/health**

- **描述**: 检查服务的健康状况。
- **成功响应 (200 OK)**:
  ```json
  {
    "status": "UP"
  }
  ```

## 4. 配置

主要的配置文件是 `src/main/resources/application.properties`。

- **数据库连接**:
  - `spring.datasource.url`: 数据库的JDBC URL。
  - `spring.datasource.username`: 数据库用户名。
  - `spring.datasource.password`: 数据库密码。

- **平台API (以Dify为例)**:
  - `dify.api.url`: Dify平台的API URL。
  - `dify.api.key`: 您的Dify平台API密钥。

## 5. 如何运行

1. **克隆项目**:
   ```bash
   git clone <repository-url>
   ```
2. **配置 `application.properties`**:
   - 更新数据库连接信息。
   - 设置您所使用平台的API URL和密钥。
3. **构建并运行项目**:
   ```bash
   mvn spring-boot:run
   ```

服务将在本地的8080端口启动。