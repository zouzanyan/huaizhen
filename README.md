# Huaizhen - 通用后台管理系统

一个基于 Spring Boot + Vue 3 的轻量级后台管理系统，提供完整的权限管理功能。

## 技术栈

**后端**
- Spring Boot 3.5.7
- MyBatis-Plus 3.5.7
- Spring Security + JWT
- MySQL 8.x

**前端**
- Vue 3.4 + Vite 5
- Element Plus
- Vue Router 4
- Axios

## 功能特性

- **用户管理** - 用户增删改查、状态管理
- **角色管理** - 角色配置、权限分配
- **菜单管理** - 动态菜单配置、树形结构
- **权限管理** - 基于RBAC的细粒度权限控制
- **安全认证** - JWT Token 认证机制

## 项目结构

```
huaizhen/
├── backend/          # 后端服务
│   └── src/main/java/org/qinfeng/backend/
│       ├── controller/   # 控制器
│       ├── service/      # 业务逻辑
│       ├── mapper/       # 数据访问
│       ├── entity/       # 实体类
│       ├── security/     # 安全配置
│       └── config/       # 应用配置
├── front/            # 前端应用
│   └── src/
│       ├── views/        # 页面组件
│       ├── components/   # 公共组件
│       ├── router/       # 路由配置
│       └── services/     # API服务
└── db/               # 数据库脚本
```

## 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- MySQL 8.0+

### 后端启动

```bash
cd backend
# 创建数据库并导入 db/mysql.sql
# 修改 application.yml 数据库配置
./mvnw spring-boot:run
```

### 前端启动

```bash
cd front
npm install
npm run dev
```

## 默认账号

- 用户名: `admin`
- 密码: `admin123456`

## License

MIT
