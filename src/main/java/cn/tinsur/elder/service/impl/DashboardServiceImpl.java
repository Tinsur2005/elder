package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.mapper.CareTaskMapper;
import cn.tinsur.elder.mapper.ContractMapper;
import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.mapper.ElderTagMapper;
import cn.tinsur.elder.mapper.TagMapper;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.entity.Contract;
import cn.tinsur.elder.pojo.entity.ElderTag;
import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.vo.DashboardVO;
import cn.tinsur.elder.pojo.vo.NameValueVO;
import cn.tinsur.elder.pojo.vo.WeekTaskVO;
import cn.tinsur.elder.service.IDashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 首页看板 服务实现类
 * 全部复用各业务模块已有的Mapper做统计，不额外建表、不写新SQL
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private CareTaskMapper careTaskMapper;

    @Autowired
    private ElderTagMapper elderTagMapper;

    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取首页看板数据（统计卡片数字 + 各图表数据）
     *
     * @return 看板数据
     */
    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();

        // 1.统计卡片：老人总数、合同总数、用户总数（逻辑删除的记录会自动被排除）
        vo.setElderCount(elderMapper.selectCount(null));
        vo.setContractCount(contractMapper.selectCount(null));
        vo.setUserCount(userMapper.selectCount(null));

        // 2.一次查出近7天（含今天）的护理任务，后面"近7天柱状图"和"今日状态饼图"都从这份列表里统计，避免重复查库
        List<CareTask> weekTasks = listWeekTasks();

        // 3.近7天护理任务完成情况（柱状图）
        vo.setWeekTaskList(buildWeekTaskList(weekTasks));

        // 4.今日护理任务状态分布（饼图），并顺便取出今日待执行数给统计卡片
        List<NameValueVO> todayTaskStatusList = buildTodayTaskStatusList(weekTasks);
        vo.setTodayTaskStatusList(todayTaskStatusList);
        vo.setTodayPendingTaskCount(todayTaskStatusList.stream()
                .filter(item -> "待执行".equals(item.getName()))
                .findFirst()
                .map(NameValueVO::getValue)
                .orElse(0L));

        // 5.合同类型分布（饼图）
        vo.setContractTypeList(buildContractTypeList());

        // 6.老人标签分布（条形图）
        vo.setElderTagList(buildElderTagList());

        return vo;
    }

    /**
     * 查询近7天（含今天）的护理任务列表
     *
     * @return 近7天的护理任务
     */
    private List<CareTask> listWeekTasks() {
        // 今天零点
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 往前推6天得到近7天的起点（计划执行日期 >= 起点）
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        Date beginDate = calendar.getTime();
        // 今天 23:59:59 作为终点（计划执行日期 <= 终点）
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();

        LambdaQueryWrapper<CareTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.between(CareTask::getPlanExecuteDate, beginDate, endDate);
        return careTaskMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 统计近7天每天的任务数量，按 待执行/已完成/已跳过 三类分别计数
     * 没有任务数据的日期也补0，保证柱状图x轴连续7天
     *
     * @param weekTasks 近7天的护理任务列表
     * @return 一天一条的统计数据（按时间从早到今天排序）
     */
    private List<WeekTaskVO> buildWeekTaskList(List<CareTask> weekTasks) {
        // 按"yyyy-MM-dd"分组，key是日期字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, List<CareTask>> dateGroupMap = weekTasks.stream()
                .filter(task -> task.getPlanExecuteDate() != null)
                .collect(Collectors.groupingBy(task -> dateFormat.format(task.getPlanExecuteDate())));

        // 从6天前开始逐天构建，保证x轴顺序是"最早 -> 今天"
        SimpleDateFormat labelFormat = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        List<WeekTaskVO> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            WeekTaskVO weekTaskVO = new WeekTaskVO();
            weekTaskVO.setDate(labelFormat.format(calendar.getTime()));
            // 取出这一天的任务列表，按状态分别计数（没有数据就是0）
            List<CareTask> dayTasks = dateGroupMap.getOrDefault(dateFormat.format(calendar.getTime()), Collections.emptyList());
            weekTaskVO.setPendingCount(countByStatus(dayTasks, 0));
            weekTaskVO.setCompletedCount(countByStatus(dayTasks, 1));
            weekTaskVO.setSkippedCount(countByStatus(dayTasks, 2));
            list.add(weekTaskVO);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * 统计今天的任务状态分布（饼图数据：待执行/已完成/已跳过）
     *
     * @param weekTasks 近7天的护理任务列表（从里面过滤出今天的）
     * @return 饼图数据
     */
    private List<NameValueVO> buildTodayTaskStatusList(List<CareTask> weekTasks) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayKey = dateFormat.format(new Date());
        // 从近7天列表里过滤出今天的任务
        List<CareTask> todayTasks = weekTasks.stream()
                .filter(task -> task.getPlanExecuteDate() != null
                        && todayKey.equals(dateFormat.format(task.getPlanExecuteDate())))
                .toList();
        // 按状态计数（状态：0待执行 1已完成 2已跳过）
        List<NameValueVO> list = new ArrayList<>();
        list.add(new NameValueVO("待执行", countByStatus(todayTasks, 0)));
        list.add(new NameValueVO("已完成", countByStatus(todayTasks, 1)));
        list.add(new NameValueVO("已跳过", countByStatus(todayTasks, 2)));
        return list;
    }

    /**
     * 统计合同类型分布（饼图数据：服务合同/入住合同/其他）
     *
     * @return 饼图数据
     */
    private List<NameValueVO> buildContractTypeList() {
        // 按合同类型分组计数
        Map<Integer, Long> typeCountMap = contractMapper.selectList(null).stream()
                .filter(contract -> contract.getContractType() != null)
                .collect(Collectors.groupingBy(Contract::getContractType, Collectors.counting()));

        // 类型编码转名称的映射，与合同页面保持一致；没有数据的类型也补0
        int[] types = {0, 1, 2};
        String[] names = {"服务合同", "入住合同", "其他"};
        List<NameValueVO> list = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            list.add(new NameValueVO(names[i], typeCountMap.getOrDefault(types[i], 0L)));
        }
        return list;
    }

    /**
     * 统计老人标签分布（条形图数据：每个标签下打标老人的数量）
     *
     * @return 条形图数据（按数量从大到小排序）
     */
    private List<NameValueVO> buildElderTagList() {
        // 1.老人-标签关联全部查出，按标签id分组计数
        Map<Long, Long> tagCountMap = elderTagMapper.selectList(null).stream()
                .filter(elderTag -> elderTag.getTagId() != null)
                .collect(Collectors.groupingBy(ElderTag::getTagId, Collectors.counting()));
        if (tagCountMap.isEmpty()) return new ArrayList<>();

        // 2.一次查出涉及的标签组装成 Map<Long, String> 回填标签名称（和护理任务页fillNames同一思路）
        Map<Long, String> tagNameMap = tagMapper.selectBatchIds(tagCountMap.keySet()).stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getName));

        // 3.组装成图表数据
        List<NameValueVO> list = new ArrayList<>();
        tagCountMap.forEach((tagId, count) -> list.add(new NameValueVO(tagNameMap.getOrDefault(tagId, "未知标签"), count)));
        // 按数量从大到小排序，条形图更直观
        list.sort(Comparator.comparingLong(NameValueVO::getValue).reversed());
        return list;
    }

    /**
     * 统计任务列表里某个状态的数量
     *
     * @param tasks  任务列表
     * @param status 状态（0待执行 1已完成 2已跳过）
     * @return 该状态的数量
     */
    private Long countByStatus(List<CareTask> tasks, Integer status) {
        return tasks.stream().filter(task -> status.equals(task.getStatus())).count();
    }
}