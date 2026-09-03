package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.CarePlan;
import cn.tinsur.elder.pojo.entity.CarePlanItem;
import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.query.CareTaskQuery;
import cn.tinsur.elder.pojo.vo.CareTaskVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 护理任务与打卡记录表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
public interface ICareTaskService extends IService<CareTask> {

    /**
     * 分页查询护理任务列表，返回带老人姓名、执行护理员姓名的 CareTaskVO
     * 查看范围由后端兜底强制：用户没有 careTask:viewAll 权限时，无论 viewScope 传什么都只返回自己的任务
     *
     * @param careTaskQuery 查询条件（老人、状态、计划执行日期范围、查看范围、分页）
     * @param token         当前登录用户的 JWT（用于解析身份、校验查看全部权限）
     * @return 分页结果
     */
    IPage<CareTaskVO> list(CareTaskQuery careTaskQuery, String token);

    /**
     * 分页查询某个老人的护理任务列表，前台手机端专用，返回带老人姓名、执行护理员姓名的CareTaskVO
     * 与list不同的是：不解析后台登录用户的权限，只按老人、状态、计划执行日期范围筛选
     *
     * @param careTaskQuery 查询条件（老人、状态、计划执行日期范围、分页）
     * @return 分页结果
     */
    IPage<CareTaskVO> listByElder(CareTaskQuery careTaskQuery);

    /**
     * 查询任务详情，返回带老人姓名、执行护理员姓名的 CareTaskVO
     *
     * @param id 任务id
     * @return 带姓名的详情，查不到返回 null
     */
    CareTaskVO getDetail(Long id);

    /**
     * 完成任务：状态置为已完成(1)，记录实际完成时间，并写入执行结果、打卡照片、备注、执行人
     *
     * @param id       任务id
     * @param careTask 需要更新的执行信息（执行结果、照片、备注、执行人）
     * @return 处理结果
     */
    Result complete(Long id, CareTask careTask);

    /**
     * 跳过/取消任务：状态置为已跳过(2)
     *
     * @param id 任务id
     * @return 处理结果
     */
    Result skip(Long id);

    /**
     * 按护理计划一次性生成整个计划周期内的所有任务：
     * 遍历 [startDate, endDate] 的每一天 × 每个护理项目，按执行周期判定该天是否执行日，
     * 是则插入一条 待执行(0) 的任务
     *
     * @param plan  护理计划（开始/结束日期必填）
     * @param items 该计划包含的护理项目列表
     */
    void generateTasksForPlan(CarePlan plan, List<CarePlanItem> items);

    /**
     * 重新生成某计划的任务（方案B）：
     * 删除该计划下所有 待执行(0) 的任务，再按当前计划配置重新生成整个周期的任务；
     * 已完成/已跳过(status=1/2)的历史打卡记录保留不动
     *
     * @param planId 计划id
     */
    void regenerateTasksForPlan(Long planId);
}