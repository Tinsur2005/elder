package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Announcement;
import cn.tinsur.elder.pojo.query.AnnouncementQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 公告表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
public interface IAnnouncementService extends IService<Announcement> {

    /**
     * 分页查询公告列表
     */
    IPage<Announcement> list(AnnouncementQuery announcementQuery);
}