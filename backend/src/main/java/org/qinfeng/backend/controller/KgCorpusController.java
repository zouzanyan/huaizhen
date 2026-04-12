package org.qinfeng.backend.controller;

import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.KgCorpus;
import org.qinfeng.backend.service.IKgCorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/kg/corpus")
public class KgCorpusController {

    @Autowired
    private IKgCorpusService kgCorpusService;

    @GetMapping("/list/{projectId}")
    public Result<List<KgCorpus>> listByProjectId(@PathVariable Long projectId) {
        List<KgCorpus> list = kgCorpusService.listByProjectId(projectId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<KgCorpus> getById(@PathVariable Long id) {
        KgCorpus corpus = kgCorpusService.getById(id);
        return Result.success(corpus);
    }

    @PostMapping
    public Result<KgCorpus> create(@RequestBody KgCorpus corpus) {
        corpus.setStatus(1);
        corpus.setCreatedAt(LocalDateTime.now());
        corpus.setUpdatedAt(LocalDateTime.now());
        kgCorpusService.save(corpus);
        return Result.success(corpus);
    }

    @PutMapping
    public Result<KgCorpus> update(@RequestBody KgCorpus corpus) {
        corpus.setUpdatedAt(LocalDateTime.now());
        kgCorpusService.updateById(corpus);
        return Result.success(corpus);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        kgCorpusService.removeById(id);
        return Result.success();
    }
}
