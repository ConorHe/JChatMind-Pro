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

### ✅ AI 回复流式输出（2026-04）

原项目 AI 回复需等待后端完整生成后才一次性推送到前端，等待体验差。本项目将 `JChatMind` 的阻塞调用改为流式输出，逐 token 通过 SSE 推送，前端实时展示打字机效果。

### ✅ MCP（Model Context Protocol）工具扩展支持（2026-04）

原项目的工具只能用 Java `@Component` 实现，每次新增工具都要修改 Java 代码并重启服务，且只能使用 Java 语言。

本项目接入 MCP 协议，将工具的实现与主服务解耦：

**好处：**
- 工具可以用**任意语言**实现（Python、Node.js、Go 等），不局限于 Java
- 新增工具**无需修改 Java 代码**，只需在 MCP Server 里加一个函数
- MCP Server 可以**独立部署**，多个 Agent 可共用同一个工具集
- 支持**按 Agent 配置**工具权限，不同 Agent 可使用不同的 MCP Server

目前已在 `mcp-server/` 下提供一个 Node.js 示例 Server，包含两个工具：
- `calculate` — 四则运算计算器
- `convert_unit` — 单位换算（温度/长度/重量）

**后续如何新增 MCP 工具：**

只需在 `mcp-server/server.js` 中添加一个 `server.tool()` 调用，无需改动 Java 代码：

```javascript
server.tool(
  "my_new_tool",            // 工具名，AI 通过此名调用
  "工具的功能描述",           // AI 根据此描述决定何时调用
  { param: z.string() },    // 入参定义（用 zod 校验）
  async ({ param }) => {
    // 工具逻辑
    return { content: [{ type: "text", text: `结果：${param}` }] };
  }
);
```

重启 MCP Server 后，在 Agent 设置的「MCP 服务器」tab 中选择对应 Server 即可生效。

### ✅ 一键启动脚本（2026-04）

原项目需要手动开两个终端分别启动前后端，且需自行在浏览器输入地址。新增项目根目录 `start-dev.bat`，双击即可同时启动前后端并自动打开浏览器，开箱即用。接入 MCP 后，脚本同时启动 MCP Server（共三个窗口）。

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

双击项目根目录的 `start-dev.bat`，自动开三个窗口分别启动 MCP Server、后端、前端，并自动打开浏览器。

或手动启动：

```bash
# MCP Server（示例，可选）
cd mcp-server && npm install && node server.js

# 后端
cd jchatmind && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 前端
cd ui && npm install && npm run dev
```

## 原项目

- 原作者：[youngyangyang04](https://github.com/youngyangyang04)
- 原仓库：[https://github.com/youngyangyang04/JChatMind](https://github.com/youngyangyang04/JChatMind)
