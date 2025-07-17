# 健康监测 (Health Monitoring) 后端服务

这是一个基于 Spring Boot 的Java后端服务，为健康监测应用提供API接口。

## 1. 功能

- **健康检查API**: 提供一个 `/api/health` 端点，用于检查服务的运行状态。
- **聊天API**: 提供一个 `/api/chat` 端点，接收用户的聊天请求，并调用SiliconCloud的大模型API获取回复。

## 2. 技术栈

- **Java 17**: 项目使用的编程语言。
- **Spring Boot 3.3.1**: 核心框架，用于快速构建独立的、生产级的Spring应用程序。
- **Spring Web**: 用于构建Web应用，包括RESTful API。
- **Spring Data JPA**: 用于简化数据访问层（DAO）的实现。
- **MySQL Connector/J**: MySQL数据库的JDBC驱动。
- **Maven**: 用于项目构建和依赖管理。

## 3. API 端点

### 3.1 健康检查

- **URL**: `/api/health`
- **方法**: `GET`
- **描述**: 检查服务的健康状况。
- **成功响应**:
  ```json
  {
    "status": "UP"
  }
  ```

### 3.2 聊天

- **URL**: `/api/chat`
- **方法**: `POST`
- **描述**: 发送聊天消息并获取回复。
- **请求体**:
  ```json
  {
    "appCode": "health_qa",
    "userInput": "你好"
  }
  ```
- **成功响应**:
  ```json
  {
    // ... SiliconCloud API 返回的聊天响应
  }
  ```

## 4. 配置

主要的配置文件是 `src/main/resources/application.properties`。

- **数据库连接**:
  - `spring.datasource.url`: 数据库的JDBC URL。
  - `spring.datasource.username`: 数据库用户名。
  - `spring.datasource.password`: 数据库密码。

- **SiliconCloud API**:
  - `siliconflow.api.url`: SiliconCloud API的URL。
  - `siliconflow.api.key`: 您的SiliconCloud API密钥。

## 5. 如何运行

1. **克隆项目**:
   ```bash
   git clone <repository-url>
   ```
2. **配置 `application.properties`**:
   - 更新数据库连接信息。
   - 设置您的SiliconCloud API密钥。
3. **构建并运行项目**:
   ```bash
   mvn spring-boot:run
   ```

服务将在本地的8080端口启动。
