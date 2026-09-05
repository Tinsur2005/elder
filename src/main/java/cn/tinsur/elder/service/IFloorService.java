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

import cn.tinsur.elder.pojo.entity.Floor;
import cn.tinsur.elder.pojo.query.FloorQuery;
import cn.tinsur.elder.pojo.vo.FloorVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 楼层表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
public interface IFloorService extends IService<Floor> {

    /**
     * 分页查询楼层列表（补充所属楼栋名称）
     * @param floorQuery 查询条件（楼栋、创建时间范围、分页）
     * @return 分页结果
     */
    IPage<FloorVO> list(FloorQuery floorQuery);

    /**
     * 获取全部楼层列表（供房间、床位等页面的楼层下拉框使用）
     * @return 楼层的List列表
     */
    List<Floor> listAll();
}