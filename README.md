# JChatMind-Pro

> 基于 [youngyangyang04/JChatMind](https://github.com/youngyangyang04/JChatMind) fork 而来，在原项目基础上持续增加和完善功能。

## 项目简介

JChatMind-Pro 是一个 AI 聊天应用，支持多模型对话、Tool Calling、RAG 知识库等能力。

**技术栈**
- **后端**：Spring Boot 3.x + Spring AI，支持 Deepseek、ZhipuAI 等模型
- **前端**：React 19 + TypeScript + Vite + Tailwind CSS
- **数据库**：PostgreSQL + MyBatis

## 相较原项目的改动

---

### ✅ 天气查询 Tool Calling 完整实现（2026-04）

原项目中 `CityTool` 和 `WeatherTool` 均为硬编码占位实现（城市固定返回"深圳"，天气固定返回"晴转多云 25°C"），本项目对其进行了真实 API 对接，实现完整的 Tool Calling 链路：

1. **DateTool** — 获取当前日期
2. **CityTool** — 通过 IP 定位获取当前城市（ip-api.com）
3. **WeatherTool** — 查询实时天气（和风天气 QWeather API）

### ✅ 一键启动脚本（2026-04）

原项目需要手动开两个终端分别启动前后端，且需自行在浏览器输入地址。新增项目根目录 `start-dev.bat`，双击即可同时启动前后端并自动打开浏览器，开箱即用。

---

## 快速开始

### 配置

复制配置模板并填入真实值：

```bash
cp jchatmind/src/main/resources/application.yaml \
   jchatmind/src/main/resources/application-local.yaml
```

编辑 `application-local.yaml`，填写数据库、邮箱、AI API Key、QWeather 等配置。

### 启动

双击项目根目录的 `start-dev.bat`，自动开两个窗口分别启动前后端。

或手动启动：

```bash
# 后端
cd jchatmind && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 前端
cd ui && npm install && npm run dev
```

## 原项目

- 原作者：[youngyangyang04](https://github.com/youngyangyang04)
- 原仓库：[https://github.com/youngyangyang04/JChatMind](https://github.com/youngyangyang04/JChatMind)
