# 健康监测系统API文档

## 基础信息
- 基础URL: `http://localhost:8080`
- 认证方式: Session认证

## 健康检查
- **URL**: `/api/health`
- **方法**: GET
- **描述**: 检查服务是否正常运行
- **响应**:
  ```json
  {"status": "UP"}
  ```

## 认证相关接口

### 用户登录
- **URL**: `/api/auth/login`
- **方法**: POST
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **成功响应**:
  ```json
  {
    "username": "string",
    "nickname": "string",
    "authenticated": true,
    "message": "登录成功"
  }
  ```

### 获取当前用户信息
- **URL**: `/api/auth/me`
- **方法**: GET
- **成功响应**:
  ```json
  {
    "username": "string",
    "nickname": "string",
    "authenticated": true,
    "message": "已登录"
  }
  ```

## 应用相关接口

### 获取所有应用
- **URL**: `/api/v1/apps`
- **方法**: GET
- **响应**:
  ```json
  [
    {
      "code": "string",
      "name": "string",
      "description": "string"
    }
  ]
  ```

## 会话相关接口

### 获取会话列表
- **URL**: `/api/v1/conversations?appCode={appCode}`
- **方法**: GET
- **参数**:
  - `appCode`: 应用代码
  - `page`: 页码（可选）
  - `size`: 每页数量（可选）
- **响应**:
  ```json
  {
    "content": [
      {
        "id": 1,
        "title": "会话标题",
        "createTime": "2023-01-01T00:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 1
  }
  ```

### 获取会话详情
- **URL**: `/api/v1/conversations/{conversationId}`
- **方法**: GET
- **响应**:
  ```json
  {
    "id": 1,
    "title": "会话标题",
    "messages": [
      {
        "content": "消息内容",
        "sender": "USER",
        "timestamp": "2023-01-01T00:00:00"
      }
    ]
  }
  ```

## 聊天相关接口

### 发送消息
- **URL**: `/api/v1/chat/stream`
- **方法**: POST
- **请求体**:
  ```json
  {
    "appCode": "string",
    "message": "string"
  }
  ```
- **响应**: SSE流式响应
  ```text
  data: {"content": "部分响应内容"}

  data: {"content": "更多响应内容"}

  data: [DONE]
  ```

## 测试建议
1. 首先调用`/api/auth/login`接口登录
2. 登录成功后，服务器会设置Session
3. 后续请求会自动携带Session信息
4. 可以测试各个接口的功能
