# Admin System

Vue 3 + Element Plus 后台管理系统示例

## 技术栈

- Vue 3
- Element Plus
- Vue Router
- TypeScript
- Vite

## 项目结构

```
src/
├── components/          # 组件
│   ├── Layout.vue      # 布局组件
│   ├── Sidebar.vue     # 侧边栏组件
│   └── Header.vue      # 头部组件
├── views/              # 页面
│   ├── Dashboard.vue   # 仪表盘
│   ├── UserManagement.vue    # 用户管理
│   ├── RoleManagement.vue    # 角色管理
│   ├── AccessLog.vue        # 访问日志
│   └── OperationLog.vue     # 操作日志
├── router/             # 路由
│   └── index.ts        # 路由配置
├── main.ts             # 入口文件
└── App.vue             # 根组件
```

## 安装依赖

```bash
npm install
```

## 运行项目

```bash
npm run dev
```

## 构建项目

```bash
npm run build
```
