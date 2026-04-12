package org.qinfeng.backend.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.qinfeng.backend.config.LlmConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.*;

@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);

    @Autowired
    private LlmConfig llmConfig;

    @Autowired
    private Neo4jService neo4jService;

    private WebClient getWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMinutes(2));
        
        return WebClient.builder()
                .baseUrl(llmConfig.getApiUrl())
                .defaultHeader("Authorization", "Bearer " + llmConfig.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public String chat(String userMessage) {
        WebClient client = getWebClient();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", llmConfig.getModel());
        requestBody.put("messages", Arrays.asList(
                Map.of("role", "user", "content", userMessage)
        ));
        requestBody.put("temperature", 0.7);

        Mono<String> response = client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);

        return response.block();
    }

    public List<Map<String, Object>> extractEntities(String text, List<String> entityTypes) {
        String prompt = buildExtractionPrompt(text, entityTypes);
        String response = chat(prompt);
        return parseExtractionResult(response);
    }

    private String buildExtractionPrompt(String text, List<String> entityTypes) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请从以下文本中抽取知识图谱实体和关系。\n\n");
        prompt.append("文本内容：\n").append(text).append("\n\n");
        prompt.append("需要抽取的实体类型：").append(String.join("、", entityTypes)).append("\n\n");
        prompt.append("请以JSON格式返回结果，格式如下：\n");
        prompt.append("{\n");
        prompt.append("  \"entities\": [\n");
        prompt.append("    {\"name\": \"实体名称\", \"type\": \"实体类型\"},\n");
        prompt.append("    ...\n");
        prompt.append("  ],\n");
        prompt.append("  \"relations\": [\n");
        prompt.append("    {\"source\": \"实体1\", \"target\": \"实体2\", \"relation\": \"关系类型\"},\n");
        prompt.append("    ...\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");
        prompt.append("请只返回JSON，不要包含其他内容。");
        return prompt.toString();
    }

    private List<Map<String, Object>> parseExtractionResult(String response) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            log.info("Parsing LLM response...");
            JSONObject jsonResponse = JSON.parseObject(response);
            if (jsonResponse.containsKey("choices")) {
                String content = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                
                log.info("LLM raw content: {}", content);
                
                content = content.trim();
                if (content.startsWith("```json")) {
                    content = content.substring(7);
                }
                if (content.startsWith("```")) {
                    content = content.substring(3);
                }
                if (content.endsWith("```")) {
                    content = content.substring(0, content.length() - 3);
                }
                content = content.trim();
                
                log.info("Cleaned content: {}", content);
                
                JSONObject extraction = JSON.parseObject(content);
                log.info("Parsed extraction: {}", extraction.toJSONString());
                result.add(extraction);
            } else {
                log.warn("No choices in LLM response: {}", response);
            }
        } catch (Exception e) {
            log.error("Failed to parse extraction result: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("rawResponse", response);
            result.add(error);
        }
        return result;
    }

    public Map<String, Object> extractAndSaveToNeo4j(String text, List<String> entityTypes, Long projectId, Map<String, String> entityTypeColors) {
        log.info("Starting extraction for project {}, entity types: {}", projectId, entityTypes);
        List<Map<String, Object>> extractionResult = extractEntities(text, entityTypes);
        
        if (extractionResult.isEmpty() || extractionResult.get(0).containsKey("error")) {
            log.error("Extraction failed: {}", extractionResult.isEmpty() ? "No result" : extractionResult.get(0));
            return extractionResult.isEmpty() ? Map.of("error", "No result") : extractionResult.get(0);
        }

        Map<String, Object> result = extractionResult.get(0);
        log.info("Extraction result: {}", JSON.toJSONString(result));
        
        Map<String, Long> entityNameToId = new HashMap<>();
        long nodeIdCounter = System.currentTimeMillis();

        if (result.containsKey("entities")) {
            JSONArray entities = (JSONArray) result.get("entities");
            log.info("Found {} entities to save", entities.size());
            for (int i = 0; i < entities.size(); i++) {
                JSONObject entity = entities.getJSONObject(i);
                String name = entity.getString("name");
                String type = entity.getString("type");
                String color = entityTypeColors.getOrDefault(type, "#409EFF");
                
                long nodeId = nodeIdCounter++;
                entityNameToId.put(name, nodeId);
                
                log.info("Saving entity: name={}, type={}, nodeId={}", name, type, nodeId);
                neo4jService.createNodeWithId(type.toUpperCase(), nodeId, projectId, name, type, color);
            }
        } else {
            log.warn("No entities found in extraction result");
        }

        if (result.containsKey("relations")) {
            JSONArray relations = (JSONArray) result.get("relations");
            log.info("Found {} relations to save", relations.size());
            for (int i = 0; i < relations.size(); i++) {
                JSONObject relation = relations.getJSONObject(i);
                String source = relation.getString("source");
                String target = relation.getString("target");
                String relationType = relation.getString("relation");
                
                Long sourceId = entityNameToId.get(source);
                Long targetId = entityNameToId.get(target);
                
                if (sourceId != null && targetId != null) {
                    neo4jService.createRelation(sourceId, targetId, relationType, projectId);
                }
            }
        }

        return Map.of(
            "success", true,
            "entityCount", entityNameToId.size(),
            "graphData", neo4jService.getGraphData(projectId)
        );
    }
}
