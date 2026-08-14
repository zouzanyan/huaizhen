package org.qinfeng.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.app.entity.Board;
import org.qinfeng.app.mapper.BoardMapper;
import org.qinfeng.app.service.IBoardService;
import org.springframework.stereotype.Service;

/**
 * 板块服务实现类
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@Service
public class BoardServiceImpl extends ServiceImpl<BoardMapper, Board> implements IBoardService {

}
