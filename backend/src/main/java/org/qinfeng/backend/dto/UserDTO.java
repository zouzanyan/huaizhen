package org.qinfeng.backend.dto;

import lombok.Data;

/**
 * 用户数据传输对象
 *
 * @author qinfeng
 */
@Data
public class UserDTO {

    /**
     * 用户ID（修改时需要）
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（新增时必填，修改时可选）
     */
    private String password;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 角色ID列表
     */
    private Long[] roleIds;
}
