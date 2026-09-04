/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝
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
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 邮箱验证码表
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EmailCode implements Serializable {


    /**
     * 验证码记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接收验证码的邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 6位数字验证码
     */
    private String code;

    /**
     * 使用场景：BIND_EMAIL绑定邮箱 CHANGE_PASSWORD邮箱改密 CHANGE_EMAIL更换邮箱
     */
    private String scene;

    /**
     * 过期时间（生成后5分钟内有效）
     */
    @TableField("expire_time")
    private Date expireTime;

    /**
     * 是否已使用：0未使用 1已使用（使用后作废）
     */
    private Integer used;

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