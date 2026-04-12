package org.qinfeng.backend.controller;

import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.KgEntityType;
import org.qinfeng.backend.service.IKgEntityTypeService;
import org.qinfeng.backend.service.LlmService;
import org.qinfeng.backend.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kg/extraction")
public class KgExtractionController {

    @Autowired
    private LlmService llmService;

    @Autowired
    private Neo4jService neo4jService;

    @Autowired
    private IKgEntityTypeService kgEntityTypeService;

    @PostMapping("/llm")
    public Result<Map<String, Object>> extractByLlm(@RequestBody Map<String, Object> request) {
        Long projectId = Long.valueOf(request.get("projectId").toString());
        String text = request.get("text").toString();
        
        List<KgEntityType> entityTypes = kgEntityTypeService.listByProjectId(projectId);
        List<String> entityTypeNames = entityTypes.stream()
                .map(KgEntityType::getName)
                .collect(Collectors.toList());
        
        Map<String, String> entityTypeColors = entityTypes.stream()
                .collect(Collectors.toMap(KgEntityType::getName, 
                        et -> et.getColor() != null ? et.getColor() : "#409EFF"));
        
        Map<String, Object> result = llmService.extractAndSaveToNeo4j(text, entityTypeNames, projectId, entityTypeColors);
        
        return Result.success(result);
    }

    @PostMapping("/llm/preview")
    public Result<Map<String, Object>> previewExtraction(@RequestBody Map<String, Object> request) {
        Long projectId = Long.valueOf(request.get("projectId").toString());
        String text = request.get("text").toString();
        
        List<KgEntityType> entityTypes = kgEntityTypeService.listByProjectId(projectId);
        List<String> entityTypeNames = entityTypes.stream()
                .map(KgEntityType::getName)
                .collect(Collectors.toList());
        
        List<Map<String, Object>> result = llmService.extractEntities(text, entityTypeNames);
        
        return Result.success(result.isEmpty() ? new HashMap<>() : result.get(0));
    }
}
