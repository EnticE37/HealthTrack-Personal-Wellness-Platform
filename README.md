# HealthTrack 个人健康与就医平台

基于 Spring Boot（Java 11）与 Vue 3（Vite） 的全栈示例，用于课程/作业展示。后端默认运行在 8080，前端运行在 5173，并通过 Vite 代理转发 `/api` 请求到后端。

## 项目结构

```
.
├── backend/        # Spring Boot 2.7 + JPA + MySQL
│   ├── pom.xml
│   ├── src/main/java/com/healthtrack/backend
│   └── src/main/resources
│       ├── application.properties
│       └── schema.sql
└── frontend/       # Vite + Vue 3 前端
    ├── index.html
    └── src
```

## 本地运行

### 环境要求
- Java 11
- Maven 3.x
- Node.js 22+
- MySQL 8（在本地创建 `healthtrack` 数据库，或执行 `backend/src/main/resources/schema.sql`）

### 启动后端
```bash
cd backend
mvn spring-boot:run
```
配置默认使用 `root/root` 连接 `healthtrack` 库，可在 `application.properties` 中调整。

### 启动前端
```bash
cd frontend
npm install
npm run dev
```
浏览器访问 http://localhost:5173 即可使用示例界面，表单会调用后端 REST API 完成账号信息维护、预约、健康挑战、统计查询等操作。

## 主要接口
- `POST /api/auth/register` 注册用户（姓名、Health ID、密码、可选邮箱/电话）。
- `POST /api/auth/login` 通过 Health ID + 密码登录。
- `PUT /api/account/{id}` 更新姓名、主治医生执业号；支持添加/删除邮箱、电话、关联医生。
- `POST /api/appointments` 创建预约；`PUT /api/appointments/{id}/cancel` 24 小时内取消。
- `POST /api/challenges` 创建健康挑战；`POST /api/challenges/invite` 发送邀请；`GET /api/challenges` 列表。
- `POST /api/summary/metrics/{userId}` 保存月度健康指标；`GET /api/summary/metrics/{userId}` 查看；`GET /api/search/appointments` 预约搜索。

前后端接口路径保持 `/api` 前缀，Vite 已配置代理避免 CORS。
