package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.backend.entity.AiRewrite;

public interface IAiRewriteService extends IService<AiRewrite> {

    AiRewrite executeRewrite(Long id);
}
