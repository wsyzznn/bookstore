# Mock 服务使用说明

## 📦 安装依赖

```bash
cd online-bookstore-frontend
npm install
```

## 🚀 启动方式

### 方式一：同时启动前端和 Mock 服务（推荐）

```bash
npm run dev:mock
```

这会同时启动：
- 前端开发服务器：http://localhost:5173
- Mock API 服务器：http://localhost:8080

### 方式二：分别启动

```bash
# 终端1 - 启动 Mock 服务器
npm run mock

# 终端2 - 启动前端
npm run dev
```

## 📮 Postman 测试

### 导入 API 集合

1. 打开 Postman
2. 点击 **Import** 按钮
3. 选择文件 `mock/postman_collection.json`
4. 导入成功后会看到 "Online Bookstore API" 集合

### API 概览

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| **认证** | `/api/auth/login` | POST | 用户登录 |
| | `/api/auth/register` | POST | 用户注册 |
| | `/api/auth/logout` | POST | 退出登录 |
| **图书** | `/api/books` | GET | 获取图书列表（支持搜索、分页） |
| | `/api/books/:id` | GET | 获取图书详情 |
| | `/api/books` | POST | 添加图书（商家） |
| | `/api/books/:id` | PUT | 更新图书 |
| | `/api/books/:id` | DELETE | 删除图书 |
| **购物车** | `/api/cart` | GET | 获取购物车 |
| | `/api/cart` | POST | 添加到购物车 |
| | `/api/cart/:id` | PUT | 更新数量 |
| | `/api/cart/:id` | DELETE | 删除购物车项 |
| **订单** | `/api/orders` | GET | 获取订单列表 |
| | `/api/orders/:id` | GET | 订单详情 |
| | `/api/orders` | POST | 创建订单 |
| | `/api/orders/:id/status` | PUT | 更新订单状态 |
| **地址** | `/api/addresses` | GET | 获取地址列表 |
| | `/api/addresses` | POST | 添加地址 |
| | `/api/addresses/:id` | PUT | 更新地址 |
| | `/api/addresses/:id` | DELETE | 删除地址 |
| **管理员** | `/api/admin/users` | GET | 用户列表 |
| | `/api/admin/orders` | GET | 所有订单 |

## 👤 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 商家 | merchant1 | merchant123 |
| 普通用户 | user1 | user123 |

## 📝 统一响应格式

所有 API 返回统一格式：

```json
{
  "code": 200,       // 状态码：200成功，401未授权，403禁止，404未找到
  "msg": "success",  // 消息
  "data": { ... }    // 数据
}
```

## 🔧 修改 Mock 数据

Mock 数据存储在 `mock/data.json`，你可以：
- 直接编辑文件修改初始数据
- 通过 API 调用增删改数据（会实时保存到文件）

## ⚠️ 切换到真实后端

后端 API 开发完成后，修改 `vite.config.js`：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',  // 改回后端地址
    changeOrigin: true,
  }
}
```

## 🔍 调试技巧

1. **查看请求日志**：Mock 服务器控制台会显示所有请求
2. **Postman 环境变量**：baseUrl 默认为 `http://localhost:8080`
3. **数据重置**：重新复制 `data.json` 原始内容即可重置数据
