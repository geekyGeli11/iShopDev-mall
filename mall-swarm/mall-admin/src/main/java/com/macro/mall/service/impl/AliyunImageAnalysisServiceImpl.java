package com.macro.mall.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.config.AliyunAiConfig;
import com.macro.mall.service.AliyunImageAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云图片分析服务实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class AliyunImageAnalysisServiceImpl implements AliyunImageAnalysisService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunImageAnalysisServiceImpl.class);
    
    @Autowired
    private AliyunAiConfig aliyunAiConfig;
    
    @Override
    public String extractStyleDescription(String imageUrl) {
        try {
            LOGGER.info("调用通义千问VL模型提取图片风格介绍，图片URL：{}", imageUrl);

            // 获取API密钥
            String apiKey = aliyunAiConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new RuntimeException("阿里云API密钥未配置");
            }

            // 构建请求体 - HTTP调用时需要将messages放入input对象中
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-vl-max");

            // 构建消息内容
            List<Map<String, Object>> contentList = new ArrayList<>();

            // 添加图片
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("image", imageUrl);
            contentList.add(imageContent);

            // 添加提示词
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("text", "请详细描述这张图片的风格特点，包括色彩、构图、氛围等方面，用简洁的文字概括其艺术风格。要求：1. 描述要简洁明了，不超过100字；2. 重点突出图片的艺术风格和视觉特征；3. 直接给出描述，不要有多余的前缀或后缀。");
            contentList.add(textContent);

            // 构建消息
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", contentList);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(userMessage);

            // HTTP调用时，messages需要放在input对象中
            Map<String, Object> input = new HashMap<>();
            input.put("messages", messages);
            requestBody.put("input", input);

            // 调用API
            String apiUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 发送请求
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(requestBody);

            LOGGER.info("请求体：{}", jsonInputString);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] inputBytes = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(inputBytes, 0, inputBytes.length);
            }

            // 读取响应
            int responseCode = conn.getResponseCode();
            LOGGER.info("响应码：{}", responseCode);

            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            br.close();

            String responseBody = response.toString();
            LOGGER.info("响应体：{}", responseBody);

            if (responseCode != 200) {
                throw new RuntimeException("API调用失败，响应码：" + responseCode + "，响应：" + responseBody);
            }

            // 解析响应
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode outputNode = rootNode.path("output");
            JsonNode choicesNode = outputNode.path("choices");

            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.path("message");
                JsonNode contentNode = messageNode.path("content");

                if (contentNode.isArray() && contentNode.size() > 0) {
                    // 内容是数组，取第一个元素的text字段
                    JsonNode firstContent = contentNode.get(0);
                    String description = firstContent.path("text").asText();

                    if (description != null && !description.isEmpty()) {
                        LOGGER.info("成功提取图片风格描述：{}", description);
                        return description;
                    }
                } else if (contentNode.isTextual()) {
                    // 内容是字符串
                    String description = contentNode.asText();

                    if (description != null && !description.isEmpty()) {
                        LOGGER.info("成功提取图片风格描述：{}", description);
                        return description;
                    }
                }
            }

            // 如果没有找到描述，返回默认值
            LOGGER.warn("未能从通义千问VL API结果中提取到风格描述，返回默认值");
            return "该图片展现了独特的艺术风格";

        } catch (Exception e) {
            LOGGER.error("通义千问VL API调用异常", e);
            throw new RuntimeException("图片分析API调用异常：" + e.getMessage());
        }
    }
    
    @Override
    public boolean isServiceAvailable() {
        try {
            String apiKey = aliyunAiConfig.getApiKey();
            return apiKey != null && !apiKey.trim().isEmpty();
        } catch (Exception e) {
            LOGGER.error("检查图片分析API服务状态失败", e);
            return false;
        }
    }
}

