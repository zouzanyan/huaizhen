package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qinfeng.backend.entity.KgCorpus;

import java.util.List;

public interface IKgCorpusService extends IService<KgCorpus> {
    List<KgCorpus> listByProjectId(Long projectId);
}
