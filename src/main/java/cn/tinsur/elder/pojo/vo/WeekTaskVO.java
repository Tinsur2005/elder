package cn.tinsur.elder.pojo.vo;

import lombok.Data;

/**
 * 近7天护理任务统计VO：一天一条，供首页柱状图使用
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Data
public class WeekTaskVO {

    /**
     * 日期（如：08-25）
     */
    private String date;

    /**
     * 待执行数量
     */
    private Long pendingCount;

    /**
     * 已完成数量
     */
    private Long completedCount;

    /**
     * 已跳过数量
     */
    private Long skippedCount;
}