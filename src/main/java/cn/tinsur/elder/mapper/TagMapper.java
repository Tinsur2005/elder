package cn.tinsur.elder.mapper;

import cn.tinsur.elder.pojo.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 标签表 Mapper 接口
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-26
 */
public interface TagMapper extends BaseMapper<Tag> {

    Long getCount(Long id);
}
