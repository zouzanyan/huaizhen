package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.dto.PageQuery;
import org.qinfeng.backend.entity.Prompt;
import org.qinfeng.backend.service.IPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sys/prompt")
public class PromptController {

    @Autowired
    private IPromptService promptService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getPromptList(
            @RequestParam(required = false) Long categoryId,
            PageQuery query) {
        Page<Prompt> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Prompt> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Prompt::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Prompt::getCreatedAt);
        Page<Prompt> result = promptService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Prompt> getPromptById(@PathVariable Long id) {
        Prompt prompt = promptService.getById(id);
        if (prompt == null) {
            return Result.error("提示词不存在");
        }
        return Result.success(prompt);
    }

    @PostMapping
    public Result<Void> addPrompt(@RequestBody Prompt prompt) {
        boolean success = promptService.save(prompt);
        if (!success) {
            return Result.error("创建提示词失败");
        }
        return Result.success("创建提示词成功");
    }

    @PutMapping
    public Result<Void> updatePrompt(@RequestBody Prompt prompt) {
        Prompt existPrompt = promptService.getById(prompt.getId());
        if (existPrompt == null) {
            return Result.error("提示词不存在");
        }
        existPrompt.setTitle(prompt.getTitle());
        existPrompt.setContent(prompt.getContent());
        existPrompt.setCategoryId(prompt.getCategoryId());
        existPrompt.setStatus(prompt.getStatus());
        boolean success = promptService.updateById(existPrompt);
        if (!success) {
            return Result.error("更新提示词失败");
        }
        return Result.success("更新提示词成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePrompt(@PathVariable Long id) {
        Prompt prompt = promptService.getById(id);
        if (prompt == null) {
            return Result.error("提示词不存在");
        }
        boolean success = promptService.removeById(id);
        if (!success) {
            return Result.error("删除提示词失败");
        }
        return Result.success("删除提示词成功");
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeletePrompts(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的提示词");
        }
        boolean success = promptService.removeByIds(List.of(ids));
        if (!success) {
            return Result.error("删除提示词失败");
        }
        return Result.success("批量删除提示词成功");
    }
}
