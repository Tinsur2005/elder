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

import cn.tinsur.elder.pojo.entity.Building;
import cn.tinsur.elder.pojo.query.BuildingQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 楼栋表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
public interface IBuildingService extends IService<Building> {

    /**
     * 分页查询楼栋列表
     * @param buildingQuery 查询条件（名称、创建时间范围、分页）
     * @return 分页结果
     */
    IPage<Building> list(BuildingQuery buildingQuery);

    /**
     * 获取全部楼栋列表（供楼层、房间、床位等页面的楼栋下拉框使用）
     * @return 楼栋的List列表
     */
    List<Building> listAll();
}