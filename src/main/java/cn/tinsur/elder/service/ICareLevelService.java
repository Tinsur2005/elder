package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.CareLevel;
import cn.tinsur.elder.pojo.query.CareLevelQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 护理等级表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
public interface ICareLevelService extends IService<CareLevel> {

    /**
     * 分页查询护理等级列表
     * @param careLevelQuery 查询条件（名称、状态、创建时间范围、分页）
     * @return 分页结果
     */
    IPage<CareLevel> list(CareLevelQuery careLevelQuery);
}