package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.CarePlan;
import cn.tinsur.elder.pojo.entity.CarePlanItem;
import cn.tinsur.elder.pojo.query.CarePlanQuery;
import cn.tinsur.elder.pojo.vo.CarePlanVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 护理计划表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
public interface ICarePlanService extends IService<CarePlan> {

    /**
     * 分页查询护理计划列表，返回带老人/护理人员/护理等级姓名的 CarePlanVO
     * @param carePlanQuery 查询条件（名称、老人、状态、创建时间范围、分页）
     * @return 分页结果
     */
    IPage<CarePlanVO> list(CarePlanQuery carePlanQuery);

    /**
     * 根据id获取该计划包含的护理项目列表
     * @param id
     * @return
     */
    Result<List<CarePlanItem>> getCareItemsById(Long id);

    /**
     * 根据计划id更新该计划包含的护理项目，先删除该计划所有项目，再插入新数据
     * @param id
     * @param carePlanItems
     * @return
     */
    Result updateCareItems(Long id, List<CarePlanItem> carePlanItems);

    /**
     * 根据计划id删除护理计划，级联删除该计划的全部任务（含已完成打卡记录）和护理项目
     * @param id 计划id
     */
    void deletePlanById(Long id);

    /**
     * 批量删除护理计划，同样级联删除各计划的全部任务和护理项目
     * @param ids 计划id集合
     */
    void deletePlanBatch(List<Long> ids);
}