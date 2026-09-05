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

import cn.tinsur.elder.pojo.entity.Room;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 房间视图对象，在房间基础上补充所属楼栋和楼层信息
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoomVO extends Room {

    /**
     * 所属楼栋ID（由楼层带出，供编辑时楼栋下拉回显）
     */
    private Long buildingId;

    /**
     * 所属楼栋名称
     */
    private String buildingName;

    /**
     * 所属楼层号
     */
    private Integer floorNo;

}