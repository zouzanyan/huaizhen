package org.qinfeng.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qinfeng.backend.entity.Forum;

import java.util.List;
import java.util.Map;

/**
 * 版块服务接口
 *
 * @author qinfeng
 * @since 2026-02-23
 */
public interface ForumService {

    /**
     * 分页查询版块列表
     */
    Page<Forum> getForumList(Integer page, Integer size, String keyword, Short status);

    /**
     * 获取所有版块（不分页）
     */
    List<Forum> getAllForums();

    /**
     * 根据ID获取版块
     */
    Forum getForumById(Long id);

    /**
     * 创建版块
     */
    boolean createForum(Forum forum);

    /**
     * 更新版块
     */
    boolean updateForum(Forum forum);

    /**
     * 删除版块
     */
    boolean deleteForum(Long id);

    /**
     * 获取版块统计信息
     */
    Map<String, Object> getForumStats();
}
