package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.backend.entity.Prompt;
import org.qinfeng.backend.mapper.PromptMapper;
import org.qinfeng.backend.service.IPromptService;
import org.springframework.stereotype.Service;

@Service
public class PromptServiceImpl extends ServiceImpl<PromptMapper, Prompt> implements IPromptService {

    @Override
    public void deleteByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Prompt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Prompt::getCategoryId, categoryId);
        this.remove(wrapper);
    }
}
