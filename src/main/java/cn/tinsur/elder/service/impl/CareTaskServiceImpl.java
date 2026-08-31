package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.mapper.CareItemMapper;
import cn.tinsur.elder.mapper.CarePlanItemMapper;
import cn.tinsur.elder.mapper.CarePlanMapper;
import cn.tinsur.elder.mapper.CareTaskMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.entity.CareItem;
import cn.tinsur.elder.pojo.entity.CarePlan;
import cn.tinsur.elder.pojo.entity.CarePlanItem;
import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.CareTaskQuery;
import cn.tinsur.elder.pojo.vo.CareTaskVO;
import cn.tinsur.elder.service.ICareTaskService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 护理任务与打卡记录表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Service
public class CareTaskServiceImpl extends ServiceImpl<CareTaskMapper, CareTask> implements ICareTaskService {

    @Autowired
    private CareTaskMapper careTaskMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CarePlanMapper carePlanMapper;

    @Autowired
    private CarePlanItemMapper carePlanItemMapper;

    @Autowired
    private CareItemMapper careItemMapper;

    /**
     * 获取护理任务列表（分页），返回 CareTaskVO，并给每个 VO 填充老人姓名、执行护理员姓名。
     * 查询前先自动生成本日护理任务，保证今天该有的任务一定出现在列表里
     *
     * @param careTaskQuery
     * @return
     */
    @Override
    public IPage<CareTaskVO> list(CareTaskQuery careTaskQuery) {
        // 0.先自动生成本日护理任务（已存在的不会重复生成）
        generateTodayTasks();

        // 1.先查护理任务分页（按老人、状态、计划执行日期范围筛选）
        IPage<CareTask> page = new Page<>(careTaskQuery.getPage(), careTaskQuery.getLimit());
        LambdaQueryWrapper<CareTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(!ObjectUtils.isEmpty(careTaskQuery.getElderId()), CareTask::getElderId, careTaskQuery.getElderId())
                .eq(!ObjectUtils.isEmpty(careTaskQuery.getStatus()), CareTask::getStatus, careTaskQuery.getStatus())
                .between(!ObjectUtils.isEmpty(careTaskQuery.getBeginPlanExecuteDate())
                                && !ObjectUtils.isEmpty(careTaskQuery.getEndPlanExecuteDate()),
                        CareTask::getPlanExecuteDate, careTaskQuery.getBeginPlanExecuteDate(),
                        careTaskQuery.getEndPlanExecuteDate())
                .orderByDesc(CareTask::getPlanExecuteDate);
        IPage<CareTask> careTaskPage = careTaskMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的CareTask转成CareTaskVO
        List<CareTaskVO> careTaskVOList = careTaskPage.getRecords().stream()
                .map(careTask -> {
                    CareTaskVO vo = new CareTaskVO();
                    BeanUtils.copyProperties(careTask, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上老人姓名、执行护理员姓名
        fillNames(careTaskVOList);

        // 4.返回CareTaskVO类型的分页
        IPage<CareTaskVO> voPage = new Page<>(careTaskPage.getCurrent(), careTaskPage.getSize(), careTaskPage.getTotal());
        voPage.setRecords(careTaskVOList);
        return voPage;
    }

    /**
     * 查询任务详情，返回带老人姓名、执行护理员姓名的 CareTaskVO
     *
     * @param id 任务id
     * @return 带姓名的详情，查不到返回 null
     */
    @Override
    public CareTaskVO getDetail(Long id) {
        CareTask careTask = careTaskMapper.selectById(id);
        if (careTask == null) return null;
        CareTaskVO vo = new CareTaskVO();
        BeanUtils.copyProperties(careTask, vo);
        // 复用填姓名逻辑：把单条包成列表回填老人姓名、执行护理员姓名
        List<CareTaskVO> list = new ArrayList<>();
        list.add(vo);
        fillNames(list);
        return vo;
    }

    /**
     * 批量给CareTaskVO填充老人姓名、执行护理员姓名
     */
    private void fillNames(List<CareTaskVO> careTaskVOList) {
        if (careTaskVOList.isEmpty()) return;

        // 1.老人姓名：取当前页所有老人id，一次查出组装成 Map<Long, String>，再回填
        List<Long> elderIds = careTaskVOList.stream()
                .map(CareTaskVO::getElderId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (!elderIds.isEmpty()) {
            Map<Long, String> elderNameMap = elderMapper.selectBatchIds(elderIds).stream()
                    .collect(Collectors.toMap(Elder::getId, Elder::getRealName));
            careTaskVOList.forEach(vo -> vo.setElderName(elderNameMap.get(vo.getElderId())));
        }

        // 2.执行护理员姓名：取当前页所有执行人id，一次查出组装成 Map<Long, String>，再回填
        List<Long> userIds = careTaskVOList.stream()
                .map(CareTaskVO::getUserId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (!userIds.isEmpty()) {
            Map<Long, String> userNameMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, User::getRealName));
            careTaskVOList.forEach(vo -> vo.setUserName(userNameMap.get(vo.getUserId())));
        }
    }

    /**
     * 完成任务：状态置为已完成(1)，记录实际完成时间，并写入执行结果、打卡照片、备注、执行人
     * 用 updateById 只更新非空字段，不触碰其他列
     *
     * @param id       任务id
     * @param careTask 执行信息（执行结果、照片、备注、执行人）
     * @return 处理结果
     */
    @Override
    public Result complete(Long id, CareTask careTask) {
        CareTask update = new CareTask();
        update.setId(id);
        update.setStatus(1);                    //已完成为1
        update.setActualExecuteTime(new Date());//实际完成时间取当前时间
        update.setExecuteResult(careTask.getExecuteResult());
        update.setExecuteImg(careTask.getExecuteImg());
        update.setRemark(careTask.getRemark());
        update.setUserId(careTask.getUserId());
        careTaskMapper.updateById(update);
        return Result.ok("已完成打卡");
    }

    /**
     * 跳过/取消任务：状态置为已跳过(2)
     *
     * @param id 任务id
     * @return 处理结果
     */
    @Override
    public Result skip(Long id) {
        CareTask update = new CareTask();
        update.setId(id);
        update.setStatus(2); //已跳过为2
        careTaskMapper.updateById(update);
        return Result.ok("已跳过该任务");
    }

    /**
     * 自动生成本日护理任务（幂等，重复调用不会重复生成）：
     * 扫描所有"进行中(status=1)"且"今天在计划周期内(开始日期<=今天,且结束日期为空或>=今天)"的护理计划，
     * 对其中每个护理项目，按执行周期+执行日判定今天是不是执行日：
     * - 每天(cycle=0)：每天都执行
     * - 每周(cycle=1)：今天星期几(周一=1~周日=7) 等于 项目的执行日才执行
     * - 每月(cycle=2)：今天几号(1~31) 等于 项目的执行日才执行
     * 满足执行日时，若该 计划+项目+今天 还没有任何任务记录（无论什么状态），
     * 就插入一条 待执行(0) 的任务；已存在则不生成，避免把已完成/已跳过的覆盖掉
     */
    private void generateTodayTasks() {
        LocalDate today = LocalDate.now();
        // 今天零点，用于和计划开始/结束日期这种 java.util.Date 比较
        Date todayDate = Date.from(today.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

        // 1.查所有进行中、今天在计划周期内的护理计划（结束日期为空视为长期计划）
        List<CarePlan> plans = carePlanMapper.selectList(
                new LambdaQueryWrapper<CarePlan>()
                        .eq(CarePlan::getStatus, 1)
                        .le(CarePlan::getStartDate, todayDate)
                        .and(w -> w.isNull(CarePlan::getEndDate).or().ge(CarePlan::getEndDate, todayDate)));
        if (plans.isEmpty()) return;

        // 2.一次查出这些计划的所有护理项目，按计划id分组，避免反复查库
        List<Long> planIds = plans.stream().map(CarePlan::getId).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, List<CarePlanItem>> itemMap = carePlanItemMapper.selectList(
                        new LambdaQueryWrapper<CarePlanItem>().in(CarePlanItem::getCarePlanId, planIds))
                .stream().collect(Collectors.groupingBy(CarePlanItem::getCarePlanId));

        // 3.把涉及的所有护理项目id一次查出项目名，组装成 Map 供写任务时取冗余名称
        Map<Long, String> careItemNameMap = itemMap.values().stream()
                .flatMap(List::stream)
                .map(CarePlanItem::getCareItemId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> {
                            CareItem careItem = careItemMapper.selectById(id);
                            return careItem == null ? null : careItem.getName();
                        }));

        // 4.遍历每个计划的项目，按执行日规则判定今天是不是执行日，是则生成任务
        for (CarePlan plan : plans) {
            List<CarePlanItem> items = itemMap.get(plan.getId());
            if (items == null || items.isEmpty()) continue;
            for (CarePlanItem item : items) {
                if (!isTaskDay(today, item)) continue; //今天不是该项目的执行日，跳过

                // 幂等校验：该 计划+项目+今天 已存在任务（不管什么状态）就不再生成
                Long exist = careTaskMapper.selectCount(
                        new LambdaQueryWrapper<CareTask>()
                                .eq(CareTask::getCarePlanId, plan.getId())
                                .eq(CareTask::getCareItemId, item.getCareItemId())
                                .eq(CareTask::getPlanExecuteDate, todayDate));
                if (exist != null && exist > 0) continue;

                // 组装并插入一条待执行任务
                CareTask task = new CareTask();
                task.setElderId(plan.getElderId());                       //老人
                task.setCarePlanId(plan.getId());                         //来源护理计划
                task.setCareItemId(item.getCareItemId());                 //护理项目
                task.setCareItemName(careItemNameMap.get(item.getCareItemId())); //项目名冗余，防止改名历史变动
                task.setUserId(plan.getUserId());                         //指定执行护理员=计划的护理人员
                task.setPlanExecuteDate(todayDate);                       //计划执行日期=今天
                task.setPlanExecuteTime(item.getExecuteTime());           //计划执行时间=项目配置的时间
                task.setStatus(0);                                        //待执行
                careTaskMapper.insert(task);
            }
        }
    }

    /**
     * 按执行周期+执行日判定"今天"是不是该护理项目的执行日
     *
     * @param today 今天
     * @param item  护理项目（含执行周期、执行日）
     * @return 今天是执行日返回 true，否则 false
     */
    private boolean isTaskDay(LocalDate today, CarePlanItem item) {
        int cycle = item.getExecuteCycle() == null ? 0 : item.getExecuteCycle(); //执行周期缺省按"每天"处理
        if (cycle == 0) return true; //每天
        if (item.getExecuteDay() == null) return false;
        if (cycle == 1) { //每周：今天星期几(周一=1~周日=7) 等于 执行日
            return today.getDayOfWeek().getValue() == item.getExecuteDay();
        }
        //每月：今天几号 等于 执行日
        return today.getDayOfMonth() == item.getExecuteDay();
    }
}