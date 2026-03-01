package org.qinfeng.backend.dto;

import lombok.Data;

/**
 * Mock用户数据传输对象
 *
 * @author qinfeng
 * @since 2026-03-01
 */
@Data
public class MockUserDTO {

    /**
     * 用户名（可选，不传则自动生成）
     */
    private String username;

    /**
     * 昵称（可选，不传则自动生成）
     */
    private String nickname;

    /**
     * 密码（可选，默认：mockmockmockhahahaha）
     */
    private String password;

    /**
     * 邮箱（可选，不传则自动生成）
     */
    private String email;

    /**
     * 头像URL（可选，不传则随机生成）
     */
    private String avatarUrl;

    /**
     * 角色（0-普通用户，1-管理员，默认：0）
     */
    private Short role;

    /**
     * 状态（0-禁用，1-启用，默认：1）
     */
    private Short status;
}
