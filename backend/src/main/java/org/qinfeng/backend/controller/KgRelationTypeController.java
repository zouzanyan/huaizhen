package org.qinfeng.backend.controller;

import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.KgRelationType;
import org.qinfeng.backend.service.IKgRelationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/kg/relation-type")
public class KgRelationTypeController {

    @Autowired
    private IKgRelationTypeService kgRelationTypeService;

    @GetMapping("/list/{projectId}")
    public Result<List<KgRelationType>> listByProjectId(@PathVariable Long projectId) {
        List<KgRelationType> list = kgRelationTypeService.listByProjectId(projectId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<KgRelationType> getById(@PathVariable Long id) {
        KgRelationType relationType = kgRelationTypeService.getById(id);
        return Result.success(relationType);
    }

    @PostMapping
    public Result<KgRelationType> create(@RequestBody KgRelationType relationType) {
        relationType.setCreatedAt(LocalDateTime.now());
        relationType.setUpdatedAt(LocalDateTime.now());
        kgRelationTypeService.save(relationType);
        return Result.success(relationType);
    }

    @PutMapping
    public Result<KgRelationType> update(@RequestBody KgRelationType relationType) {
        relationType.setUpdatedAt(LocalDateTime.now());
        kgRelationTypeService.updateById(relationType);
        return Result.success(relationType);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        kgRelationTypeService.removeById(id);
        return Result.success();
    }
}
