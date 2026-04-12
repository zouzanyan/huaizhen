package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.backend.entity.PromptCategory;
import org.qinfeng.backend.mapper.PromptCategoryMapper;
import org.qinfeng.backend.service.IPromptCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PromptCategoryServiceImpl extends ServiceImpl<PromptCategoryMapper, PromptCategory> implements IPromptCategoryService {

    @Autowired
    private PromptCategoryMapper promptCategoryMapper;

    @Override
    public List<Map<String, Object>> listAllWithPromptCount() {
        return promptCategoryMapper.selectAllWithPromptCount();
    }
}
