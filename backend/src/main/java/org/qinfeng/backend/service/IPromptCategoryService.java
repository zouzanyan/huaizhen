package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.backend.entity.PromptCategory;

import java.util.List;
import java.util.Map;

public interface IPromptCategoryService extends IService<PromptCategory> {

    List<Map<String, Object>> listAllWithPromptCount();
}
