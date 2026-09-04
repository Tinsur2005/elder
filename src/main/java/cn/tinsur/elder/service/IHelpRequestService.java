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
package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.query.HelpRequestQuery;
import cn.tinsur.elder.pojo.vo.HelpRequestVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.tinsur.elder.pojo.entity.HelpRequest;

/**
 * <p>
 * 求助记录表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
public interface IHelpRequestService extends IService<HelpRequest> {

    /**
     * 分页查询求助列表，返回带老人姓名、处理人姓名的 HelpRequestVO
     * @param helpRequestQuery 查询条件（老人、类型、紧急程度、状态、创建时间范围、分页）
     * @return 分页结果
     */
    IPage<HelpRequestVO> list(HelpRequestQuery helpRequestQuery);

    /**
     * 提交处理，填写处理结果后将该求助置为已处理，处理后不允许再次处理
     * @param id 求助记录id
     * @param result 处理结果说明
     * @param handlerId 处理人id（当前登录管理员）
     * @return 处理结果
     */
    Result handle(Long id, String result, Long handlerId);

    /**
     * 忽略求助，将该求助置为已忽略，处理后不允许再次处理
     * @param id 求助记录id
     * @param handlerId 处理人id（当前登录管理员）
     * @return 处理结果
     */
    Result ignore(Long id, Long handlerId);
}