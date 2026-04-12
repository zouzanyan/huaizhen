package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.backend.entity.KgEntityType;
import org.qinfeng.backend.mapper.KgEntityTypeMapper;
import org.qinfeng.backend.service.IKgEntityTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KgEntityTypeServiceImpl extends ServiceImpl<KgEntityTypeMapper, KgEntityType> implements IKgEntityTypeService {

    @Override
    public List<KgEntityType> listByProjectId(Long projectId) {
        return list(new LambdaQueryWrapper<KgEntityType>()
                .eq(KgEntityType::getProjectId, projectId)
                .orderByAsc(KgEntityType::getId));
    }
}
