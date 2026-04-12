package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.dto.PageQuery;
import org.qinfeng.backend.entity.AiRewrite;
import org.qinfeng.backend.service.IAiRewriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/rewrite")
public class AiRewriteController {

    @Autowired
    private IAiRewriteService rewriteService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getRewriteList(PageQuery query) {
        Page<AiRewrite> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<AiRewrite> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(AiRewrite::getTitle, query.getKeyword())
                    .or().like(AiRewrite::getOriginalText, query.getKeyword()));
        }

        if (query.getStatus() != null) {
            wrapper.eq(AiRewrite::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(AiRewrite::getCreatedAt);
        Page<AiRewrite> result = rewriteService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<AiRewrite> getRewriteById(@PathVariable Long id) {
        AiRewrite rewrite = rewriteService.getById(id);
        if (rewrite == null) {
            return Result.error("记录不存在");
        }
        return Result.success(rewrite);
    }

    @PostMapping
    public Result<AiRewrite> addRewrite(@RequestBody AiRewrite rewrite) {
        rewrite.setStatus(0);
        boolean success = rewriteService.save(rewrite);
        if (!success) {
            return Result.error("创建记录失败");
        }
        return Result.success(rewrite);
    }

    @PutMapping
    public Result<Void> updateRewrite(@RequestBody AiRewrite rewrite) {
        AiRewrite exist = rewriteService.getById(rewrite.getId());
        if (exist == null) {
            return Result.error("记录不存在");
        }

        if (exist.getStatus() == 1) {
            return Result.error("任务正在处理中，无法修改");
        }

        exist.setTitle(rewrite.getTitle());
        exist.setOriginalText(rewrite.getOriginalText());
        exist.setPromptId(rewrite.getPromptId());
        exist.setApiId(rewrite.getApiId());
        exist.setModelName(rewrite.getModelName());

        boolean success = rewriteService.updateById(exist);
        if (!success) {
            return Result.error("更新记录失败");
        }
        return Result.success("更新记录成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRewrite(@PathVariable Long id) {
        AiRewrite rewrite = rewriteService.getById(id);
        if (rewrite == null) {
            return Result.error("记录不存在");
        }

        if (rewrite.getStatus() == 1) {
            return Result.error("任务正在处理中，无法删除");
        }

        boolean success = rewriteService.removeById(id);
        if (!success) {
            return Result.error("删除记录失败");
        }
        return Result.success("删除记录成功");
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeleteRewrites(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的记录");
        }
        boolean success = rewriteService.removeByIds(List.of(ids));
        if (!success) {
            return Result.error("删除记录失败");
        }
        return Result.success("批量删除成功");
    }

    @PostMapping("/{id}/execute")
    public Result<AiRewrite> executeRewrite(@PathVariable Long id) {
        try {
            AiRewrite rewrite = rewriteService.executeRewrite(id);
            return Result.success(rewrite);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/execute")
    public Result<AiRewrite> executeAndSave(@RequestBody AiRewrite rewrite) {
        rewrite.setStatus(0);
        rewriteService.save(rewrite);
        
        try {
            AiRewrite result = rewriteService.executeRewrite(rewrite.getId());
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
