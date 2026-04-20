package com.kama.jchatmind.agent.tools;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class CityTool implements Tool {

    private final WebClient webClient;

    public CityTool(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://ip-api.com").build();
    }

    @Override
    public String getName() {
        return "cityTool";
    }

    @Override
    public String getDescription() {
        return "获取当前的城市";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @org.springframework.ai.tool.annotation.Tool(name = "getCity", description = "获取当前的城市")
    public String getCity() {
        try {
            IpApiResponse response = webClient.get()
                    .uri("/json")
                    .retrieve()
                    .bodyToMono(IpApiResponse.class)
                    .block();
            if (response != null && "success".equals(response.getStatus()) && response.getCity() != null) {
                log.info("IP 定位城市: {}", response.getCity());
                return response.getCity();
            }
            log.warn("IP 定位返回失败状态: {}", response);
            return "未知城市";
        } catch (Exception e) {
            log.error("获取城市失败", e);
            return "城市信息获取失败，请稍后重试";
        }
    }

    @Data
    private static class IpApiResponse {
        private String status;
        private String city;
    }
}
