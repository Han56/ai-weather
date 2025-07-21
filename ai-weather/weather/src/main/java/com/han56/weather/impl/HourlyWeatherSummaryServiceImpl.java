package com.han56.weather.impl;

import com.alibaba.fastjson2.JSONObject;
import com.han56.weather.controller.WeatherServiceController;
import com.han56.weather.models.entity.ForecastHourly;
import com.han56.weather.models.response.ForecastHourlyMojiResponse;
import com.han56.weather.service.HourlyWeatherSummaryService;
import com.han56.weather.service.WeatherForecastService;
import com.han56.weather.utils.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import java.util.Arrays;
import java.util.List;

@Service
public class HourlyWeatherSummaryServiceImpl implements HourlyWeatherSummaryService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceController.class);

    @Autowired
    private WeatherForecastService weatherForecastService;
    @Autowired
    private ChatClient chatClient;

    @Override
    @Async("aiWeatherSummaryExecutor")
    public void getHourlyWeatherSummary(DeferredResult<String> result, String cityId) {
        try {
            // 参数校验
            if (cityId == null || cityId.trim().isEmpty()) {
                result.setErrorResult("Error: cityId cannot be null or empty");
                return;
            }

            //1.根据cityId获取24小时天气
            ServiceResult<ForecastHourlyMojiResponse> forecastHourlyMojiResponseServiceResult = weatherForecastService.forecastHourlyWeather(cityId);

            //2.检查服务调用结果
            if (forecastHourlyMojiResponseServiceResult == null) {
                result.setErrorResult("Error: Weather service returned null response");
                return;
            }

            if (!forecastHourlyMojiResponseServiceResult.isSuccess()) {
                String errorMsg = forecastHourlyMojiResponseServiceResult.getMessage();
                result.setErrorResult("Error: Weather service failed - " + (errorMsg != null ? errorMsg : "Unknown error"));
                return;
            }

            ForecastHourlyMojiResponse response = forecastHourlyMojiResponseServiceResult.getResult();
            if (response == null) {
                result.setErrorResult("Error: Weather data response is null");
                return;
            }

            //3.提取出预测信息
            if (response.getForecastHourlyMojiData() == null) {
                result.setErrorResult("Error: Weather forecast data is null");
                return;
            }

            List<ForecastHourly> forecastHourlyList = response.getForecastHourlyMojiData().getForecastHourlyList();
            if (forecastHourlyList == null || forecastHourlyList.isEmpty()) {
                result.setErrorResult("Error: No hourly forecast data available");
                return;
            }

            //4.组装提示词信息
            SystemMessage systemMessage = new SystemMessage(
                "# 你是一个气象学天气预报专家：" +
                "你将收到一份 userMessage 提供的未来24小时天气预报数据（JSON数组），请严格按照 userMessage 给出的 JSON 信息进行分析和判断，" +
                "不要凭空编造信息，所有结论都必须基于 userMessage 的数据。" +
                "请输出未来5小时的天气总结，只输出JSON，不要有多余解释或自然语言，JSON结构如下：" +
                "{\n" +
                "  \"precipitation\": {\n" +
                "    \"summary\": \"\",\n" +
                "    \"hours\": [\n" +
                "      {\"hour\": \"\", \"condition\": \"\"},\n" +
                "      {\"hour\": \"\", \"condition\": \"\"},\n" +
                "      {\"hour\": \"\", \"condition\": \"\"},\n" +
                "      {\"hour\": \"\", \"condition\": \"\"},\n" +
                "      {\"hour\": \"\", \"condition\": \"\"}\n" +
                "    ]\n" +
                "  },\n" +
                "  \"temperature\": {\n" +
                "    \"max\": 0,\n" +
                "    \"min\": 0,\n" +
                "    \"summary\": \"\",\n" +
                "    \"humidity_analysis\": \"\"\n" +
                "  },\n" +
                "  \"wind\": {\n" +
                "    \"summary\": \"\"\n" +
                "  },\n" +
                "  \"uv\": {\n" +
                "    \"summary\": \"\",\n" +
                "    \"range\": {\"max\": 0, \"min\": 0},\n" +
                "    \"trend\": \"\"\n" +
                "  }\n" +
                "}\n" +
                "请严格按照上述JSON结构输出，字段名和嵌套结构都不要变，所有内容都用中文。" +
                "precipitation.hours 数组必须严格按照 userMessage 里前5个小时的 JSON 数组，" +
                "每个元素的 hour 和 condition 字段直接取自 userMessage 里的同名字段，原样输出，不要省略，不要更改顺序，不要输出多余信息。" +
                "如果没有数据请填空字符串或null，不要省略字段。"
            );
            UserMessage userMessage = new UserMessage(JSONObject.toJSONString(forecastHourlyList.subList(0, Math.min(5, forecastHourlyList.size()))));
            Prompt prompt = new Prompt(Arrays.asList(userMessage, systemMessage));

            //5. 调用AI
            String aiRes = chatClient.call(prompt).getResult().getOutput().getContent();
            if (aiRes == null || aiRes.trim().isEmpty()) {
                result.setErrorResult("Error: AI response is empty");
                return;
            }

            String json = extractFirstJsonObject(aiRes);
            result.setResult(json);

        } catch (Exception e) {
            logger.error("Error in processWeatherAsync for cityId: " + cityId, e);
            String errorMessage = "Error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error occurred");
            result.setErrorResult(errorMessage);
        }
    }

    /**
     * 提取第一个合法JSON对象字符串（支持嵌套）
     */
    public static String extractFirstJsonObject(String text) {
        int start = text.indexOf('{');
        if (start == -1) return text;
        int count = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') count++;
            if (c == '}') count--;
            if (count == 0) {
                return text.substring(start, i + 1);
            }
        }
        return text; // fallback: 没有完整的JSON对象
    }
} 