package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.dto.PageQuery;
import org.qinfeng.backend.entity.KgProject;
import org.qinfeng.backend.security.JwtUser;
import org.qinfeng.backend.service.IKgProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/kg/project")
public class KgProjectController {

    @Autowired
    private IKgProjectService kgProjectService;

    @GetMapping("/list")
    public Result<Page<KgProject>> list(PageQuery pageQuery) {
        Page<KgProject> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        Page<KgProject> result = kgProjectService.page(page);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<KgProject> getById(@PathVariable Long id) {
        KgProject project = kgProjectService.getById(id);
        return Result.success(project);
    }

    @PostMapping
    public Result<KgProject> create(@RequestBody KgProject project) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        project.setCreateUserId(jwtUser.getUserId());
        project.setStatus(1);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        kgProjectService.save(project);
        return Result.success(project);
    }

    @PutMapping
    public Result<KgProject> update(@RequestBody KgProject project) {
        project.setUpdatedAt(LocalDateTime.now());
        kgProjectService.updateById(project);
        return Result.success(project);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        kgProjectService.removeById(id);
        return Result.success();
    }
}
