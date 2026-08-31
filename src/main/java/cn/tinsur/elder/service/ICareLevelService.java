package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.CareLevel;
import cn.tinsur.elder.pojo.query.CareLevelQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 获取全部启用状态的护理等级列表（供护理计划等"选护理等级"下拉框使用）
     * @return 护理等级的List列表
     */
    List<CareLevel> listAll();
}