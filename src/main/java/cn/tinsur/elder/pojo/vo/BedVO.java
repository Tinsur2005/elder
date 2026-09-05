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

import cn.tinsur.elder.pojo.entity.Bed;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 床位视图对象，在床位基础上补充所属楼栋和楼层信息
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BedVO extends Bed {

    /**
     * 所属楼栋ID（由楼层带出，供编辑时楼栋下拉回显）
     */
    private Long buildingId;

    /**
     * 所属楼层ID（由房间带出，供编辑时楼层下拉回显）
     */
    private Long floorId;

    /**
     * 所属楼栋名称
     */
    private String buildingName;

    /**
     * 所属楼层号
     */
    private Integer floorNo;

    /**
     * 所属房间号
     */
    private String roomNo;

    /**
     * 入住老人姓名
     */
    private String elderName;

}