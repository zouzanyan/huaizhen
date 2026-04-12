package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.backend.entity.KgRelationType;

import java.util.List;

public interface IKgRelationTypeService extends IService<KgRelationType> {
    List<KgRelationType> listByProjectId(Long projectId);
}
