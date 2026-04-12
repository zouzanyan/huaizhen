package org.qinfeng.backend.controller;

import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.KgEntityType;
import org.qinfeng.backend.service.IKgEntityTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/kg/entity-type")
public class KgEntityTypeController {

    @Autowired
    private IKgEntityTypeService kgEntityTypeService;

    @GetMapping("/list/{projectId}")
    public Result<List<KgEntityType>> listByProjectId(@PathVariable Long projectId) {
        List<KgEntityType> list = kgEntityTypeService.listByProjectId(projectId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<KgEntityType> getById(@PathVariable Long id) {
        KgEntityType entityType = kgEntityTypeService.getById(id);
        return Result.success(entityType);
    }

    @PostMapping
    public Result<KgEntityType> create(@RequestBody KgEntityType entityType) {
        entityType.setCreatedAt(LocalDateTime.now());
        entityType.setUpdatedAt(LocalDateTime.now());
        kgEntityTypeService.save(entityType);
        return Result.success(entityType);
    }

    @PutMapping
    public Result<KgEntityType> update(@RequestBody KgEntityType entityType) {
        entityType.setUpdatedAt(LocalDateTime.now());
        kgEntityTypeService.updateById(entityType);
        return Result.success(entityType);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        kgEntityTypeService.removeById(id);
        return Result.success();
    }
}
