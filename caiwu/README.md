# Caiwu Web 管理端

基于 Spring Boot 3 + JDK 17 + Vue 3 + MySQL 的示例。

- 用户注册 / 登录，JWT 认证
- RBAC 权限模型（权限 -> 角色 -> 用户），默认管理员 / 普通用户
- 管理端用户 CRUD
- Vue 3 + Vite + Pinia + Axios 前端

## 后端运行

1. 使用 JDK 17（确保 `java -version` 输出 17）。
2. MySQL 创建数据库 `caiwu`，在 `src/main/resources/application.properties` 配置用户名/密码。
3. `mvn clean spring-boot:run` 或 IDE 运行 `CaiwuApplication`。
4. 默认管理员：`admin / Admin@123`。

主要接口：
- `POST /api/auth/register` 注册
- `POST /api/auth/login` 登录并返回 JWT
- `GET /api/users` 分页查询（需权限）
- `POST /api/users` 新增
- `PUT /api/users/{id}` 更新
- `DELETE /api/users/{id}` 删除

## 前端运行

1. 进入 `frontend`，执行 `npm install`。
2. `npm run dev`（默认 5173）；生产 `npm run build`。
3. 若后端非 `http://localhost:8080`，设置 `VITE_API_BASE_URL`。

## 默认角色与权限

| 角色  | 权限                        |
| ----- | --------------------------- |
| ADMIN | USER_READ/CREATE/UPDATE/DELETE |
| USER  | USER_READ                  |

新注册用户自动分配 `USER` 角色；管理员可在管理端调整角色与启用状态。

## 测试账号

- 管理员：`admin / Admin@123`
- 新注册用户：默认启用，仅 `USER_READ` 权限。
