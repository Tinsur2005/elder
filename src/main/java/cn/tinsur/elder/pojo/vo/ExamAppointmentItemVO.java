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
package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.ExamAppointmentItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExamAppointmentItemVO extends ExamAppointmentItem {
    /**
     * 结果类型：0文本 1数值（联表查询后填充，供结果录入时区分输入方式）
     */
    private Integer resultType;

    /**
     * 参考范围下限（联表查询后填充，供结果录入时自动判定是否异常）
     */
    private BigDecimal referenceMin;

    /**
     * 参考范围上限（联表查询后填充，供结果录入时自动判定是否异常）
     */
    private BigDecimal referenceMax;

    /**
     * 参考范围单位（联表查询后填充，便于前端展示）
     */
    private String referenceUnit;
}