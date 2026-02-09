package org.qinfeng.backend.dto;

import lombok.Data;

/**
 * 分页查询参数
 *
 * @author qinfeng
 */
@Data
public class PageQuery {

    /**
     * 当前页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 关键词（用户名或编码等）
     */
    private String keyword;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
}
