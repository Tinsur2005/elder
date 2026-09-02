package cn.tinsur.elder.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 老人预约/体检记录表
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExamAppointment implements Serializable {


    /**
     * 体检记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 老人ID
     */
    @TableField("elder_id")
    private Long elderId;

    /**
     * 体检套餐ID
     */
    @TableField("package_id")
    private Long packageId;

    /**
     * 预约/体检日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("appointment_date")
    private Date appointmentDate;

    /**
     * 预约/体检时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    @TableField("appointment_time")
    private Date appointmentTime;

    /**
     * 体检套餐价格
     */
    private BigDecimal price;

    /**
     * 状态：0待体检 1体检中 2已完成 3已取消 4已过期
     */
    private Integer status;

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
