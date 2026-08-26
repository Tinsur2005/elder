package cn.tinsur.elder.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class Elder {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String password;
    private String avatar;
    private String idCardNo;
    private int status;
    private String phone;
    private Date birthday;
    private String address;
    private String remark;

    /**
     * 逻辑删除（0：未删除，1：已删除）
     */
    @TableLogic
    private Integer deleted;

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
