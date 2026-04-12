package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.backend.entity.KgCorpus;
import org.qinfeng.backend.mapper.KgCorpusMapper;
import org.qinfeng.backend.service.IKgCorpusService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KgCorpusServiceImpl extends ServiceImpl<KgCorpusMapper, KgCorpus> implements IKgCorpusService {

    @Override
    public List<KgCorpus> listByProjectId(Long projectId) {
        return list(new LambdaQueryWrapper<KgCorpus>()
                .eq(KgCorpus::getProjectId, projectId)
                .orderByDesc(KgCorpus::getCreatedAt));
    }
}
