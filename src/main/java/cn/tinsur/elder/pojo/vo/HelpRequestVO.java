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

import cn.tinsur.elder.pojo.entity.HelpRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 求助记录展示对象，联表查询后填充老人姓名和处理人姓名，便于前端展示
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HelpRequestVO extends HelpRequest {
    /**
     * 求助老人姓名（联表查询后填充，便于前端展示）
     */
    private String elderName;

    /**
     * 处理人姓名（联表查询后填充，便于前端展示）
     */
    private String handlerName;
}