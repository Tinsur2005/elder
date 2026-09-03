/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
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
     * 查看范围：mine=仅看我的，all=查看全部。
     * 该参数只在前端有 careTask:viewAll 权限时才会被后端采纳，否则一律强制按当前登录用户过滤
     */
    private String viewScope;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer limit;
}