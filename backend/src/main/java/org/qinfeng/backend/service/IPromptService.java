package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.backend.entity.Prompt;

public interface IPromptService extends IService<Prompt> {

    void deleteByCategoryId(Long categoryId);
}
