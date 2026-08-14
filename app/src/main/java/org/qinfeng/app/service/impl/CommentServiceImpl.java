package org.qinfeng.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.qinfeng.app.entity.Comment;
import org.qinfeng.app.mapper.CommentMapper;
import org.qinfeng.app.service.ICommentService;
import org.springframework.stereotype.Service;

/**
 * 评论服务实现类
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
