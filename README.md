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

原项目中 `CityTool` 和 `WeatherTool` 均为占位实现，无论输入什么城市和日期，始终返回硬编码字符串，无法用于生产环境。本项目对其进行了完整的真实 API 对接。

#### CityTool

| | 原项目 | 本项目 |
|---|---|---|
| 实现方式 | 硬编码 | 调用 ip-api.com 接口，基于 IP 自动定位 |
| 返回值 | 永远返回 `"深圳"` | 返回真实城市名，如 `"Chengdu"` |

```java
// 原项目
public String getCity() {
    return "深圳"; // 硬编码
}

// 本项目
public String getCity() {
    IpApiResponse response = webClient.get().uri("/json")
            .retrieve().bodyToMono(IpApiResponse.class).block();
    return response.getCity(); // 真实 IP 定位
}
```

#### WeatherTool

| | 原项目 | 本项目 |
|---|---|---|
| 实现方式 | 硬编码 | 调用和风天气 QWeather API |
| 返回值 | 永远返回 `"晴转多云，温度 25°C，湿度 60%"` | 返回实时天气数据 |
| API 调用链 | 无 | Geo API 城市解析 → Weather API 实时天气 |
| 认证方式 | 无 | JWT Bearer Token |

```java
// 原项目
public String getWeather(String city, String date) {
    return city + date + "的天气查询结果：晴转多云，温度 25°C，湿度 60%"; // 硬编码
}

// 本项目：两步真实 API 调用
// Step 1: 城市名 → locationId（QWeather Geo API）
// Step 2: locationId → 实时天气（QWeather Weather API）
```

#### 完整调用链路

AI Agent 现可自动串联三个 Tool 完成天气查询：

```
用户: "今天天气怎么样？"
  → DateTool.getDate()        // 获取当前日期: 2026-04-21
  → CityTool.getCity()        // IP 定位城市: Chengdu
  → WeatherTool.getWeather()  // 实时天气: 阴，13°C，湿度 100%，北风 2 级
AI: "成都今天阴天，气温 13°C，湿度较高，注意保暖。"
```

#### 其他调整
- 三个 Tool 从 `agent/tools/test/` 迁移至 `agent/tools/`（正式 package）
- 引入 `reactor-netty-http` 解决 QWeather 响应 gzip 压缩解析问题
- 新增 `application-local.yaml` 敏感配置隔离方案

---

## 快速开始

### 配置

复制配置模板并填入真实值：

```bash
cp jchatmind/src/main/resources/application.yaml \
   jchatmind/src/main/resources/application-local.yaml
```

编辑 `application-local.yaml`，填写数据库、邮箱、AI API Key、QWeather 等配置。

### 启动后端

```bash
cd jchatmind
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 启动前端

```bash
cd ui
npm install
npm run dev
```

## 原项目

- 原作者：[youngyangyang04](https://github.com/youngyangyang04)
- 原仓库：[https://github.com/youngyangyang04/JChatMind](https://github.com/youngyangyang04/JChatMind)
