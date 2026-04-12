package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.qinfeng.backend.entity.KgCorpus;
import org.qinfeng.backend.entity.KgEntityType;
import org.qinfeng.backend.entity.KgProject;
import org.qinfeng.backend.entity.KgRelationType;
import org.qinfeng.backend.service.*;
import org.qinfeng.backend.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private IKgProjectService projectService;

    @Autowired
    private IKgEntityTypeService entityTypeService;

    @Autowired
    private IKgRelationTypeService relationTypeService;

    @Autowired
    private IKgCorpusService corpusService;

    @Autowired
    private Neo4jService neo4jService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long projectCount = projectService.count();
        long entityTypeCount = entityTypeService.count();
        long relationTypeCount = relationTypeService.count();
        long corpusCount = corpusService.count();
        long nodeCount = neo4jService.getTotalNodeCount();
        long relationCount = neo4jService.getTotalRelationCount();

        stats.put("projectCount", projectCount);
        stats.put("entityTypeCount", entityTypeCount);
        stats.put("relationTypeCount", relationTypeCount);
        stats.put("corpusCount", corpusCount);
        stats.put("nodeCount", nodeCount);
        stats.put("relationCount", relationCount);

        return Result.success(stats);
    }

    @GetMapping("/recentProjects")
    public Result<List<Map<String, Object>>> getRecentProjects() {
        QueryWrapper<KgProject> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at").last("LIMIT 5");
        List<KgProject> projects = projectService.list(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (KgProject project : projects) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", project.getId());
            item.put("name", project.getName());
            item.put("description", project.getDescription());
            item.put("createdAt", project.getCreatedAt());
            item.put("nodeCount", neo4jService.getNodeCountByProjectId(project.getId()));
            item.put("relationCount", neo4jService.getRelationCountByProjectId(project.getId()));
            result.add(item);
        }

        return Result.success(result);
    }

    @GetMapping("/recentCorpus")
    public Result<List<Map<String, Object>>> getRecentCorpus() {
        QueryWrapper<KgCorpus> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at").last("LIMIT 5");
        List<KgCorpus> corpusList = corpusService.list(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (KgCorpus corpus : corpusList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", corpus.getId());
            item.put("title", corpus.getTitle());
            item.put("source", corpus.getSource());
            item.put("status", corpus.getStatus());
            item.put("createdAt", corpus.getCreatedAt());
            if (corpus.getProjectId() != null) {
                KgProject project = projectService.getById(corpus.getProjectId());
                if (project != null) {
                    item.put("projectName", project.getName());
                }
            }
            result.add(item);
        }

        return Result.success(result);
    }
}
