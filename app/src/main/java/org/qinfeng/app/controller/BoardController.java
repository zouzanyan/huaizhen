package org.qinfeng.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.qinfeng.app.common.Result;
import org.qinfeng.app.dto.PageQuery;
import org.qinfeng.app.entity.Board;
import org.qinfeng.app.service.IBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 板块管理控制器
 *
 * @author qinfeng
 * @since 2026-08-14
 */
@RestController
@RequestMapping("/api/app/board")
public class BoardController {

    @Autowired
    private IBoardService boardService;

    /**
     * 分页查询板块列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getBoardList(PageQuery query) {
        Page<Board> page = new Page<>(query.getPage(), query.getSize());

        LambdaQueryWrapper<Board> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Board::getName, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Board::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(Board::getSort);

        Page<Board> result = boardService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());

        return Result.success(data);
    }

    /**
     * 获取所有启用的板块（下拉选择用）
     */
    @GetMapping("/all")
    public Result<List<Board>> getAllBoards() {
        LambdaQueryWrapper<Board> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Board::getStatus, 1);
        wrapper.orderByAsc(Board::getSort);
        return Result.success(boardService.list(wrapper));
    }

    /**
     * 获取板块详情
     */
    @GetMapping("/{id}")
    public Result<Board> getBoardById(@PathVariable Long id) {
        Board board = boardService.getById(id);
        if (board == null) {
            return Result.error("板块不存在");
        }
        return Result.success(board);
    }

    /**
     * 新增板块
     */
    @PostMapping
    public Result<Void> addBoard(@RequestBody Board board) {
        LambdaQueryWrapper<Board> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Board::getName, board.getName());
        if (boardService.count(wrapper) > 0) {
            return Result.error("板块名称已存在");
        }

        if (board.getStatus() == null) {
            board.setStatus(1);
        }
        if (board.getSort() == null) {
            board.setSort(0);
        }

        boolean success = boardService.save(board);
        return success ? Result.success("创建板块成功") : Result.error("创建板块失败");
    }

    /**
     * 修改板块
     */
    @PutMapping
    public Result<Void> updateBoard(@RequestBody Board board) {
        Board exist = boardService.getById(board.getId());
        if (exist == null) {
            return Result.error("板块不存在");
        }

        if (StringUtils.hasText(board.getName()) && !board.getName().equals(exist.getName())) {
            LambdaQueryWrapper<Board> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Board::getName, board.getName()).ne(Board::getId, board.getId());
            if (boardService.count(wrapper) > 0) {
                return Result.error("板块名称已存在");
            }
            exist.setName(board.getName());
        }
        if (board.getDescription() != null) {
            exist.setDescription(board.getDescription());
        }
        if (board.getStatus() != null) {
            exist.setStatus(board.getStatus());
        }
        if (board.getSort() != null) {
            exist.setSort(board.getSort());
        }

        boolean success = boardService.updateById(exist);
        return success ? Result.success("更新板块成功") : Result.error("更新板块失败");
    }

    /**
     * 删除板块
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBoard(@PathVariable Long id) {
        Board board = boardService.getById(id);
        if (board == null) {
            return Result.error("板块不存在");
        }
        boolean success = boardService.removeById(id);
        return success ? Result.success("删除板块成功") : Result.error("删除板块失败");
    }

    /**
     * 批量删除板块
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteBoards(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的板块");
        }
        boolean success = boardService.removeByIds(List.of(ids));
        return success ? Result.success("批量删除板块成功") : Result.error("删除板块失败");
    }

    /**
     * 启用/禁用板块
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateBoardStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Board board = boardService.getById(id);
        if (board == null) {
            return Result.error("板块不存在");
        }

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值不正确");
        }

        board.setStatus(status);
        boolean success = boardService.updateById(board);
        return success
                ? Result.success(status == 1 ? "启用板块成功" : "禁用板块成功")
                : Result.error("更新状态失败");
    }
}
