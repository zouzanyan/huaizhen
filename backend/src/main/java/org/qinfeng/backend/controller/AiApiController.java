package org.qinfeng.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.dto.PageQuery;
import org.qinfeng.backend.entity.AiApi;
import org.qinfeng.backend.service.IAiApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/api")
public class AiApiController {

    @Autowired
    private IAiApiService aiApiService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getAiApiList(PageQuery query) {
        Page<AiApi> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<AiApi> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(AiApi::getName, query.getKeyword())
                    .or().like(AiApi::getModelName, query.getKeyword())
                    .or().like(AiApi::getProvider, query.getKeyword()));
        }

        if (query.getStatus() != null) {
            wrapper.eq(AiApi::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(AiApi::getCreatedAt);
        Page<AiApi> result = aiApiService.page(page, wrapper);

        result.getRecords().forEach(api -> {
            if (api.getApiKey() != null && api.getApiKey().length() > 8) {
                String masked = api.getApiKey().substring(0, 4) + "****" + 
                        api.getApiKey().substring(api.getApiKey().length() - 4);
                api.setApiKey(masked);
            }
        });

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }

    @GetMapping("/all")
    public Result<List<AiApi>> getAllAiApis() {
        LambdaQueryWrapper<AiApi> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiApi::getStatus, 1);
        wrapper.select(AiApi::getId, AiApi::getName, AiApi::getProvider, 
                AiApi::getModelName, AiApi::getBaseUrl);
        wrapper.orderByAsc(AiApi::getName);
        List<AiApi> list = aiApiService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<AiApi> getAiApiById(@PathVariable Long id) {
        AiApi api = aiApiService.getById(id);
        if (api == null) {
            return Result.error("配置不存在");
        }
        return Result.success(api);
    }

    @PostMapping
    public Result<Void> addAiApi(@RequestBody AiApi aiApi) {
        LambdaQueryWrapper<AiApi> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiApi::getName, aiApi.getName());
        AiApi exist = aiApiService.getOne(wrapper);
        if (exist != null) {
            return Result.error("配置名称已存在");
        }

        boolean success = aiApiService.save(aiApi);
        if (!success) {
            return Result.error("创建配置失败");
        }
        return Result.success("创建配置成功");
    }

    @PutMapping
    public Result<Void> updateAiApi(@RequestBody AiApi aiApi) {
        AiApi exist = aiApiService.getById(aiApi.getId());
        if (exist == null) {
            return Result.error("配置不存在");
        }

        LambdaQueryWrapper<AiApi> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiApi::getName, aiApi.getName());
        wrapper.ne(AiApi::getId, aiApi.getId());
        AiApi duplicate = aiApiService.getOne(wrapper);
        if (duplicate != null) {
            return Result.error("配置名称已存在");
        }

        exist.setName(aiApi.getName());
        exist.setProvider(aiApi.getProvider());
        exist.setBaseUrl(aiApi.getBaseUrl());
        if (aiApi.getApiKey() != null && !aiApi.getApiKey().contains("****")) {
            exist.setApiKey(aiApi.getApiKey());
        }
        exist.setModelName(aiApi.getModelName());
        exist.setMaxTokens(aiApi.getMaxTokens());
        exist.setTemperature(aiApi.getTemperature());
        exist.setStatus(aiApi.getStatus());
        exist.setRemark(aiApi.getRemark());

        boolean success = aiApiService.updateById(exist);
        if (!success) {
            return Result.error("更新配置失败");
        }
        return Result.success("更新配置成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAiApi(@PathVariable Long id) {
        AiApi api = aiApiService.getById(id);
        if (api == null) {
            return Result.error("配置不存在");
        }
        boolean success = aiApiService.removeById(id);
        if (!success) {
            return Result.error("删除配置失败");
        }
        return Result.success("删除配置成功");
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeleteAiApis(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的配置");
        }
        boolean success = aiApiService.removeByIds(List.of(ids));
        if (!success) {
            return Result.error("删除配置失败");
        }
        return Result.success("批量删除配置成功");
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        AiApi api = aiApiService.getById(id);
        if (api == null) {
            return Result.error("配置不存在");
        }

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值不正确");
        }

        api.setStatus(status);
        boolean success = aiApiService.updateById(api);
        if (!success) {
            return Result.error("更新状态失败");
        }
        return Result.success(status == 1 ? "启用成功" : "禁用成功");
    }
}
