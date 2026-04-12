package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.qinfeng.backend.entity.AiApi;
import org.qinfeng.backend.entity.AiRewrite;
import org.qinfeng.backend.entity.Prompt;
import org.qinfeng.backend.mapper.AiRewriteMapper;
import org.qinfeng.backend.service.IAiApiService;
import org.qinfeng.backend.service.IAiRewriteService;
import org.qinfeng.backend.service.IPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class AiRewriteServiceImpl extends ServiceImpl<AiRewriteMapper, AiRewrite> implements IAiRewriteService {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Autowired
    private IAiApiService aiApiService;

    @Autowired
    private IPromptService promptService;

    @Autowired
    @Qualifier("proxyOkHttpClient")
    private OkHttpClient okHttpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public AiRewrite executeRewrite(Long id) {
        log.info("开始执行仿写任务, id={}", id);
        
        AiRewrite rewrite = this.getById(id);
        if (rewrite == null) {
            log.error("记录不存在, id={}", id);
            throw new RuntimeException("记录不存在");
        }

        if (rewrite.getStatus() == 1) {
            log.warn("任务正在处理中, id={}", id);
            throw new RuntimeException("任务正在处理中");
        }

        AiApi apiConfig = aiApiService.getById(rewrite.getApiId());
        if (apiConfig == null) {
            log.error("API配置不存在, apiId={}", rewrite.getApiId());
            rewrite.setStatus(3);
            rewrite.setErrorMsg("API配置不存在");
            this.updateById(rewrite);
            return rewrite;
        }

        String promptContent = "";
        if (rewrite.getPromptId() != null) {
            Prompt prompt = promptService.getById(rewrite.getPromptId());
            if (prompt != null) {
                promptContent = prompt.getContent();
                log.info("加载提示词, promptId={}, title={}", prompt.getId(), prompt.getTitle());
            }
        }

        rewrite.setStatus(1);
        this.updateById(rewrite);

        try {
            log.info("调用AI API开始, provider={}, model={}", apiConfig.getProvider(), apiConfig.getModelName());
            String result = callAiApi(apiConfig, promptContent, rewrite.getOriginalText());
            rewrite.setResultText(result);
            rewrite.setStatus(2);
            rewrite.setErrorMsg(null);
            log.info("仿写任务完成, id={}, 结果长度={}", id, result != null ? result.length() : 0);
        } catch (Exception e) {
            log.error("仿写任务失败, id={}, error={}", id, e.getMessage(), e);
            rewrite.setStatus(3);
            rewrite.setErrorMsg(e.getMessage());
        }

        this.updateById(rewrite);
        return rewrite;
    }

    private String callAiApi(AiApi apiConfig, String prompt, String originalText) {
        String provider = apiConfig.getProvider();
        String baseUrl = apiConfig.getBaseUrl();
        String apiKey = apiConfig.getApiKey();
        String modelName = apiConfig.getModelName();
        Integer maxTokens = apiConfig.getMaxTokens();
        Double temperature = apiConfig.getTemperature() != null ? apiConfig.getTemperature().doubleValue() : 0.7;

        String systemPrompt = prompt;
        String userContent = originalText;

        try {
            if ("openai".equalsIgnoreCase(provider) || "azure".equalsIgnoreCase(provider) || "custom".equalsIgnoreCase(provider)) {
                return callOpenAiCompatibleApi(baseUrl, apiKey, modelName, systemPrompt, userContent, maxTokens, temperature);
            } else if ("anthropic".equalsIgnoreCase(provider)) {
                return callAnthropicApi(baseUrl, apiKey, modelName, systemPrompt, userContent, maxTokens, temperature);
            } else if ("google".equalsIgnoreCase(provider)) {
                return callGoogleApi(baseUrl, apiKey, modelName, systemPrompt, userContent, maxTokens, temperature);
            } else {
                return callOpenAiCompatibleApi(baseUrl, apiKey, modelName, systemPrompt, userContent, maxTokens, temperature);
            }
        } catch (Exception e) {
            log.error("调用AI API异常, provider={}, model={}, error={}", provider, modelName, e.getMessage(), e);
            throw new RuntimeException("调用AI API失败: " + e.getMessage());
        }
    }

    private String callOpenAiCompatibleApi(String baseUrl, String apiKey, String model,
            String systemPrompt, String userContent, Integer maxTokens, Double temperature) throws IOException {
        String url = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);

        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);
        }
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userContent);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        log.info("OpenAI API请求, url={}, model={}, maxTokens={}, temperature={}, apiKey={}", 
                url, model, maxTokens, temperature, maskApiKey(apiKey));
        log.debug("OpenAI API请求体: {}", jsonBody);

        long startTime = System.currentTimeMillis();
        try (Response response = okHttpClient.newCall(request).execute()) {
            long elapsed = System.currentTimeMillis() - startTime;
            String responseBody = response.body() != null ? response.body().string() : "";
            
            log.info("OpenAI API响应, status={}, elapsed={}ms", response.code(), elapsed);
            log.debug("OpenAI API响应体: {}", responseBody);

            if (!response.isSuccessful()) {
                log.error("OpenAI API调用失败, status={}, body={}", response.code(), responseBody);
                throw new RuntimeException("API调用失败: " + response.code() + " - " + responseBody);
            }

            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").path(0).path("message").path("content").asText();
            log.info("OpenAI API调用成功, 响应内容长度={}", content != null ? content.length() : 0);
            return content;
        }
    }

    private String callAnthropicApi(String baseUrl, String apiKey, String model,
            String systemPrompt, String userContent, Integer maxTokens, Double temperature) throws IOException {
        String url = baseUrl.endsWith("/") ? baseUrl + "messages" : baseUrl + "/messages";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("system", systemPrompt);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userContent);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .post(body)
                .build();

        log.info("Anthropic API请求, url={}, model={}, maxTokens={}, temperature={}, apiKey={}", 
                url, model, maxTokens, temperature, maskApiKey(apiKey));
        log.debug("Anthropic API请求体: {}", jsonBody);

        long startTime = System.currentTimeMillis();
        try (Response response = okHttpClient.newCall(request).execute()) {
            long elapsed = System.currentTimeMillis() - startTime;
            String responseBody = response.body() != null ? response.body().string() : "";
            
            log.info("Anthropic API响应, status={}, elapsed={}ms", response.code(), elapsed);
            log.debug("Anthropic API响应体: {}", responseBody);

            if (!response.isSuccessful()) {
                log.error("Anthropic API调用失败, status={}, body={}", response.code(), responseBody);
                throw new RuntimeException("API调用失败: " + response.code() + " - " + responseBody);
            }

            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("content").path(0).path("text").asText();
            log.info("Anthropic API调用成功, 响应内容长度={}", content != null ? content.length() : 0);
            return content;
        }
    }

    private String callGoogleApi(String baseUrl, String apiKey, String model,
            String systemPrompt, String userContent, Integer maxTokens, Double temperature) throws IOException {
        String url = baseUrl.endsWith("/")
                ? baseUrl + "models/" + model + ":generateContent?key=" + apiKey
                : baseUrl + "/models/" + model + ":generateContent?key=" + apiKey;

        Map<String, Object> requestBody = new HashMap<>();

        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> systemPart = new HashMap<>();
            systemPart.put("text", systemPrompt + "\n\n" + userContent);
            parts.add(systemPart);
        } else {
            Map<String, String> userPart = new HashMap<>();
            userPart.put("text", userContent);
            parts.add(userPart);
        }
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("maxOutputTokens", maxTokens);
        generationConfig.put("temperature", temperature);
        requestBody.put("generationConfig", generationConfig);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        log.info("Google API请求, url={}, model={}, maxTokens={}, temperature={}, apiKey={}", 
                baseUrl + "models/" + model + ":generateContent", model, maxTokens, temperature, maskApiKey(apiKey));
        log.debug("Google API请求体: {}", jsonBody);

        long startTime = System.currentTimeMillis();
        try (Response response = okHttpClient.newCall(request).execute()) {
            long elapsed = System.currentTimeMillis() - startTime;
            String responseBody = response.body() != null ? response.body().string() : "";
            
            log.info("Google API响应, status={}, elapsed={}ms", response.code(), elapsed);
            log.debug("Google API响应体: {}", responseBody);

            if (!response.isSuccessful()) {
                log.error("Google API调用失败, status={}, body={}", response.code(), responseBody);
                throw new RuntimeException("API调用失败: " + response.code() + " - " + responseBody);
            }

            JsonNode root = objectMapper.readTree(responseBody);
            String result = root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();
            log.info("Google API调用成功, 响应内容长度={}", result != null ? result.length() : 0);
            return result;
        }
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
