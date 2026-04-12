package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.backend.entity.KgRelationType;
import org.qinfeng.backend.mapper.KgRelationTypeMapper;
import org.qinfeng.backend.service.IKgRelationTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KgRelationTypeServiceImpl extends ServiceImpl<KgRelationTypeMapper, KgRelationType> implements IKgRelationTypeService {

    @Override
    public List<KgRelationType> listByProjectId(Long projectId) {
        return list(new LambdaQueryWrapper<KgRelationType>()
                .eq(KgRelationType::getProjectId, projectId)
                .orderByAsc(KgRelationType::getId));
    }
}
