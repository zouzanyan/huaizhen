package org.qinfeng.backend.dto;

import lombok.Data;

/**
 * 角色数据传输对象
 *
 * @author qinfeng
 */
@Data
public class RoleDTO {

    /**
     * 角色ID（修改时需要）
     */
    private Long id;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
}
