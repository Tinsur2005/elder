package cn.tinsur.elder.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 护理计划和项目关联表
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CarePlanItem implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 计划id
     */
    @TableField("care_plan_id")
    private Long carePlanId;

    /**
     * 项目id
     */
    @TableField("care_item_id")
    private Long careItemId;

    /**
     * 计划执行时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    @TableField("execute_time")
    private Date executeTime;

    /**
     * 执行日：执行周期为每周时存周几（1-7,1=周一），为每月时存几号（1-31），每天则为空
     */
    @TableField("execute_day")
    private Integer executeDay;

    /**
     * 执行周期 0 天 1 周 2月
     */
    @TableField("execute_cycle")
    private Integer executeCycle;

    /**
     * 执行频次
     */
    @TableField("execute_frequency")
    private Integer executeFrequency;

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
