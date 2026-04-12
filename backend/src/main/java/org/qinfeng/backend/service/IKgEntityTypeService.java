package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.backend.entity.KgEntityType;

import java.util.List;

public interface IKgEntityTypeService extends IService<KgEntityType> {
    List<KgEntityType> listByProjectId(Long projectId);
}
