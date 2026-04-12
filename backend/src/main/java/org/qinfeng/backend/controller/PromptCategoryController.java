package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.dto.PageQuery;
import org.qinfeng.backend.entity.PromptCategory;
import org.qinfeng.backend.service.IPromptCategoryService;
import org.qinfeng.backend.service.IPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sys/prompt-category")
public class PromptCategoryController {

    @Autowired
    private IPromptCategoryService promptCategoryService;

    @Autowired
    private IPromptService promptService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getCategoryList(PageQuery query) {
        Page<PromptCategory> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<PromptCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PromptCategory::getSort);
        Page<PromptCategory> result = promptCategoryService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }

    @GetMapping("/all")
    public Result<List<Map<String, Object>>> getAllCategories() {
        List<Map<String, Object>> categories = promptCategoryService.listAllWithPromptCount();
        return Result.success(categories);
    }

    @PostMapping
    public Result<Void> addCategory(@RequestBody PromptCategory category) {
        boolean success = promptCategoryService.save(category);
        if (!success) {
            return Result.error("创建分类失败");
        }
        return Result.success("创建分类成功");
    }

    @PutMapping
    public Result<Void> updateCategory(@RequestBody PromptCategory category) {
        PromptCategory existCategory = promptCategoryService.getById(category.getId());
        if (existCategory == null) {
            return Result.error("分类不存在");
        }
        existCategory.setName(category.getName());
        existCategory.setSort(category.getSort());
        boolean success = promptCategoryService.updateById(existCategory);
        if (!success) {
            return Result.error("更新分类失败");
        }
        return Result.success("更新分类成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        PromptCategory category = promptCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        promptService.deleteByCategoryId(id);
        boolean success = promptCategoryService.removeById(id);
        if (!success) {
            return Result.error("删除分类失败");
        }
        return Result.success("删除分类成功");
    }
}
