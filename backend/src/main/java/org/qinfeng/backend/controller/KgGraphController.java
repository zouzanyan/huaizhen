package org.qinfeng.backend.controller;

import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/kg/graph")
public class KgGraphController {

    @Autowired
    private Neo4jService neo4jService;

    @GetMapping("/data/{projectId}")
    public Result<Map<String, Object>> getGraphData(@PathVariable Long projectId) {
        Map<String, Object> graphData = neo4jService.getGraphData(projectId);
        return Result.success(graphData);
    }

    @DeleteMapping("/clear/{projectId}")
    public Result<Void> clearProjectData(@PathVariable Long projectId) {
        neo4jService.clearProjectData(projectId);
        return Result.success();
    }
}
