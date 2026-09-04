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
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 求助记录表
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HelpRequest implements Serializable {


    /**
     * 求助记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 求助老人ID（关联elder.id）
     */
    @TableField("elder_id")
    private Long elderId;

    /**
     * 求助类型：0健康 1生活 2安全 3其他
     */
    private Integer type;

    /**
     * 紧急程度：0普通 1紧急 2非常紧急
     */
    private Integer urgency;

    /**
     * 求助内容（老人填写）
     */
    private String content;

    /**
     * 状态：0未处理 1已处理 2已忽略
     */
    private Integer status;

    /**
     * 处理结果说明（提交已处理时填写）
     */
    private String result;

    /**
     * 处理人ID（关联user.id，提交已处理或忽略的管理员）
     */
    @TableField("handler_id")
    private Long handlerId;

    /**
     * 处理时间（提交已处理或忽略的时间）
     */
    @TableField("handle_time")
    private Date handleTime;

    /**
     * 求助时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}