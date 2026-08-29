package cn.tinsur.elder.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 合同表
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Contract implements Serializable {


    /**
     * 合同id(主键)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contractNo;

    /**
     * 绑定的老人id(elder.id)
     */
    @TableField("elder_id")
    private Long elderId;

    /**
     * 合同名称(如:入住服务合同)
     */
    @TableField("contract_name")
    private String contractName;

    /**
     * 合同类型(0:服务合同 1:入住合同 2:其他)
     */
    @TableField("contract_type")
    private Integer contractType;

    /**
     * 合同签订/创建时间(业务字段)
     */
    @TableField("sign_time")
    private Date signTime;

    /**
     * 合同过期时间
     */
    @TableField("expire_time")
    private Date expireTime;

    /**
     * 合同文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除(0:未删除 1:已删除)
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间(系统自动填充)
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间(系统自动填充)
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
