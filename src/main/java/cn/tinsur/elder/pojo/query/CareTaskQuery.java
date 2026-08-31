package cn.tinsur.elder.pojo.query;

import lombok.Data;

import java.util.Date;

@Data
public class CareTaskQuery {
    /**
     * 老人id（按老人筛选）
     */
    private Long elderId;

    /**
     * 任务状态（0：待执行，1：已完成，2：已跳过/取消）
     */
    private Integer status;

    /**
     * 计划执行日期范围起
     */
    private Date beginPlanExecuteDate;

    /**
     * 计划执行日期范围止
     */
    private Date endPlanExecuteDate;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer limit;
}