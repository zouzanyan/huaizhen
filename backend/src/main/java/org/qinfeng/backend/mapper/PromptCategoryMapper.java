package org.qinfeng.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.qinfeng.backend.entity.PromptCategory;

import java.util.List;
import java.util.Map;

public interface PromptCategoryMapper extends BaseMapper<PromptCategory> {

    @Select("SELECT c.*, (SELECT COUNT(*) FROM prompt WHERE category_id = c.id) as prompt_count " +
            "FROM prompt_category c ORDER BY c.sort ASC")
    List<Map<String, Object>> selectAllWithPromptCount();
}
