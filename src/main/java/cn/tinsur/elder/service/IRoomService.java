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

import cn.tinsur.elder.pojo.entity.Room;
import cn.tinsur.elder.pojo.query.RoomQuery;
import cn.tinsur.elder.pojo.vo.RoomVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
public interface IRoomService extends IService<Room> {

    /**
     * 分页查询房间列表（补充所属楼栋名称和楼层号）
     * @param roomQuery 查询条件（楼栋、楼层、创建时间范围、分页）
     * @return 分页结果
     */
    IPage<RoomVO> list(RoomQuery roomQuery);

    /**
     * 根据ID查询房间（补充楼栋ID、楼栋名称和楼层号，供编辑抽屉级联回显）
     * @param id 房间ID
     * @return 房间视图对象
     */
    RoomVO getVOById(Long id);

    /**
     * 获取全部房间列表（供床位管理等页面的房间下拉框使用）
     * @return 房间的List列表
     */
    List<Room> listAll();
}