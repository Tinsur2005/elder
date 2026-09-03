/*
 *
 *  * ============================================================
 *  *
 *  *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *  *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *  *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *  *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *  *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *  *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *  *
 *  *  项目名称 : 智慧社区养老系统
 *  *  源码作者 : Tinsur (tinsur.cn)
 *  *  作者主页 : https://www.tinsur.cn
 *  *  联系方式 : me@tinsur.cn
 *  *  开源协议 : GPL 3.0
 *  *
 *  * ============================================================
 *
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
 * 资讯表
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class News implements Serializable {


    /**
     * 资讯ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资讯分类ID（关联news_category.id）
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 资讯标题
     */
    private String title;

    /**
     * 封面图片URL（列表/详情页展示，走通用上传接口）
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 资讯摘要（列表页展示，为空时前台可自行截取正文前100字）
     */
    private String summary;

    /**
     * 资讯正文（富文本编辑器生成的HTML，图片以URL内嵌）
     */
    private String content;

    /**
     * 作者（为空时前台可显示“管理员”）
     */
    private String author;

    /**
     * 阅读量（前台每打开一次详情 +1）
     */
    private Integer views;

    /**
     * 状态（0：下架，1：发布）。前台只展示已发布的资讯
     */
    private Integer status;

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
