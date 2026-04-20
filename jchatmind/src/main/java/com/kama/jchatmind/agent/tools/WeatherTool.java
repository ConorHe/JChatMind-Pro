package com.kama.jchatmind.agent.tools;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Slf4j
@Component
public class WeatherTool implements Tool {

    private final WebClient qweatherClient;
    private final String apiKey;

    public WeatherTool(WebClient.Builder builder,
                       @Value("${weather.qweather.api-token}") String apiKey,
                       @Value("${weather.qweather.api-host}") String apiHost) {
        this.apiKey = apiKey;
        HttpClient nettyClient = HttpClient.create().compress(true);
        this.qweatherClient = builder
                .baseUrl("https://" + apiHost)
                .clientConnector(new ReactorClientHttpConnector(nettyClient))
                .build();
    }

    @Override
    public String getName() {
        return "weatherTool";
    }

    @Override
    public String getDescription() {
        return "获取天气";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @org.springframework.ai.tool.annotation.Tool(name = "weather", description = "获取天气")
    public String getWeather(String city, String date) {
        try {
            // Step 1: 城市名 → locationId（QWeather Geo API）
            GeoApiResponse geoResponse = qweatherClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geo/v2/city/lookup")
                            .queryParam("location", city)
                            .build())
                    .header("Authorization", "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(GeoApiResponse.class)
                    .block();

            if (geoResponse == null || !"200".equals(geoResponse.getCode())
                    || geoResponse.getLocation() == null || geoResponse.getLocation().isEmpty()) {
                log.warn("城市 [{}] 地理位置查找失败, code={}", city,
                        geoResponse != null ? geoResponse.getCode() : "null");
                return String.format("无法查询城市 [%s] 的天气信息，城市名称无法识别", city);
            }

            String locationId = geoResponse.getLocation().get(0).getId();
            log.info("城市 [{}] 对应 locationId: {}", city, locationId);

            // Step 2: locationId → 实时天气（QWeather Weather API）
            WeatherApiResponse weatherResponse = qweatherClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v7/weather/now")
                            .queryParam("location", locationId)
                            .build())
                    .header("Authorization", "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(WeatherApiResponse.class)
                    .block();

            if (weatherResponse == null || !"200".equals(weatherResponse.getCode())
                    || weatherResponse.getNow() == null) {
                log.warn("城市 [{}] 天气查询失败, code={}", city,
                        weatherResponse != null ? weatherResponse.getCode() : "null");
                return String.format("%s %s 的天气查询失败，请稍后重试", city, date);
            }

            NowWeather now = weatherResponse.getNow();
            log.info("城市 [{}] 天气查询成功: {}", city, now.getText());

            return String.format(
                    "%s %s 的天气查询结果：%s，温度 %s°C，湿度 %s%%，%s %s 级",
                    city, date, now.getText(), now.getTemp(),
                    now.getHumidity(), now.getWindDir(), now.getWindScale()
            );
        } catch (Exception e) {
            log.error("天气查询发生异常, city={}, date={}", city, date, e);
            return String.format("%s %s 的天气查询失败：服务暂时不可用，请稍后重试", city, date);
        }
    }

    @Data
    private static class GeoLocation {
        private String id;
        private String name;
    }

    @Data
    private static class GeoApiResponse {
        private String code;
        private List<GeoLocation> location;
    }

    @Data
    private static class NowWeather {
        private String text;
        private String temp;
        private String humidity;
        private String windDir;
        private String windScale;
    }

    @Data
    private static class WeatherApiResponse {
        private String code;
        private NowWeather now;
    }
}
