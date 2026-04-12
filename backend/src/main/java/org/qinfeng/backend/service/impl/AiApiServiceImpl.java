package org.qinfeng.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.backend.entity.AiApi;
import org.qinfeng.backend.mapper.AiApiMapper;
import org.qinfeng.backend.service.IAiApiService;
import org.springframework.stereotype.Service;

@Service
public class AiApiServiceImpl extends ServiceImpl<AiApiMapper, AiApi> implements IAiApiService {

}
