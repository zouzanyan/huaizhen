package org.qinfeng.backend.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树形结构 VO
 *
 * @author qinfeng
 */
@Getter
@Setter
public class MenuTreeVO {

    private Long id;

    private String name;

    private Long parentId;

    private Integer orderNo;

    private String permissionCode;

    private String path;

    private String icon;

    private Integer status;

    /**
     * 子菜单列表
     */
    private List<MenuTreeVO> children = new ArrayList<>();
}
