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
package cn.tinsur.elder.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 体检记录明细表
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExamAppointmentItem implements Serializable {


    /**
     * 体检记录明细ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 体检记录ID
     */
    @TableField("appointment_id")
    private Long appointmentId;

    /**
     * 体检项目ID
     */
    @TableField("exam_item_id")
    private Long examItemId;

    /**
     * 项目名称快照
     */
    @TableField("item_name")
    private String itemName;

    /**
     * 数值型结果
     */
    @TableField("result_value")
    private BigDecimal resultValue;

    /**
     * 结果单位
     */
    @TableField("result_unit")
    private String resultUnit;

    /**
     * 文本型结果
     */
    @TableField("result_text")
    private String resultText;

    /**
     * 状态：0待检查 1正常 2异常 3未完成
     */
    private Integer status;

    /**
     * 是否异常：0正常 1异常
     */
    private Integer abnormal;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
