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

import cn.tinsur.elder.pojo.entity.CarePlan;
import cn.tinsur.elder.pojo.entity.CarePlanItem;
import lombok.Data;

import java.util.List;

@Data
public class CarePlanVO extends CarePlan {
    /**
     * 绑定的老人姓名（联表查询后填充，便于前端展示）
     */
    private String elderName;

    /**
     * 护理人员姓名（联表查询后填充，便于前端展示）
     */
    private String userName;

    /**
     * 护理等级名称（联表查询后填充，便于前端展示）
     */
    private String careLevelName;

    /**
     * 该计划包含的护理项目列表（AI工具查询护理计划时填充，前端暂不使用）
     */
    private List<CarePlanItem> items;
}