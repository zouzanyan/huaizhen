package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.backend.entity.KgProject;
import org.qinfeng.backend.mapper.KgProjectMapper;
import org.qinfeng.backend.service.IKgProjectService;
import org.springframework.stereotype.Service;

@Service
public class KgProjectServiceImpl extends ServiceImpl<KgProjectMapper, KgProject> implements IKgProjectService {
}
