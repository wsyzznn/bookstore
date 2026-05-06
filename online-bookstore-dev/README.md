# 前端本地运行（仅前端 + Mock）

## 概要
本说明只覆盖前端项目如何在本地运行（含内置 Mock 服务），便于前端开发或交付给后端同学进行联调。

## 先决条件
- Node.js（建议 18+）与 npm 已安装
- Windows / macOS / Linux 均适用（示例使用 PowerShell / bash）

## 快速启动（最小步骤）
```bash
cd online-bookstore-frontend
npm install
npm run dev:mock
```

- 启动后：
	- 前端 + Mock 同时运行（默认监听 http://localhost:8080）
	- 若只需 Mock：`npm run mock`
	- 若只需前端（连接真实后端，参见 env 配置）：`npm run dev`



## 关键文件
- Axios 全局： `online-bookstore-frontend/src/services/api.js`（已启用 `withCredentials: true`）
- Vite proxy： `online-bookstore-frontend/vite.config.js`
- Mock 服务脚本： `online-bookstore-frontend/mock/server.js`
- Mock 数据： `online-bookstore-frontend/mock/data.json`
- Postman 集合： `online-bookstore-frontend/mock/postman_collection.json`

## 验证接口（快速）
```bash
curl "http://localhost:8080/api/books/search"

curl.exe -X POST "http://localhost:8080/api/user/login" -H "Content-Type: application/json" -d '{"username":"user1","password":"user123"}'
```

## 常见问题与排查
- 端口被占用（8080）：
```powershell
netstat -ano | findstr 8080
taskkill /PID <pid> /F
```
- 缓存导致 304 或老数据：打开 DevTools → Network → 勾选 “Disable cache”，刷新页面。
- 页面无数据但接口返回正常：检查 Network → Response，确认 JSON 字段名与前端期望一致。

## 调试建议
- 使用 Postman 导入 `online-bookstore-frontend/mock/postman_collection.json` 逐接口验证。
- 关键流程（登录 → 加入购物车 → 结账）每步都看 Network 的请求/响应与响应头。


