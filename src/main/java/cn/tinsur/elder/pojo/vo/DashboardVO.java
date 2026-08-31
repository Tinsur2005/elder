package cn.tinsur.elder.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 首页看板数据VO：统计卡片数字 + 各图表数据，一次请求全部带回
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Data
public class DashboardVO {

    /**
     * 老人总数
     */
    private Long elderCount;

    /**
     * 合同总数
     */
    private Long contractCount;

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 今日待执行任务数
     */
    private Long todayPendingTaskCount;

    /**
     * 今日护理任务状态分布（饼图：待执行/已完成/已跳过）
     */
    private List<NameValueVO> todayTaskStatusList;

    /**
     * 近7天护理任务完成情况（柱状图：每天的待执行/已完成/已跳过数量）
     */
    private List<WeekTaskVO> weekTaskList;

    /**
     * 合同类型分布（饼图：服务合同/入住合同/其他）
     */
    private List<NameValueVO> contractTypeList;

    /**
     * 老人标签分布（条形图：每个标签下打标老人的数量）
     */
    private List<NameValueVO> elderTagList;
}