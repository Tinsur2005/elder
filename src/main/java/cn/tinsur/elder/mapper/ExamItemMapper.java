package cn.tinsur.elder.mapper;

import cn.tinsur.elder.pojo.entity.ExamItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 体检项目表 Mapper 接口
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
public interface ExamItemMapper extends BaseMapper<ExamItem> {

    /**
     * 统计体检项目被体检套餐引用的数量
     * @param ids 体检项目ID集合
     * @return 引用数量
     */
    Long countInPackage(@Param("ids") List<Long> ids);
}