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
package cn.tinsur.elder.controller.app;

import cn.tinsur.elder.pojo.entity.Announcement;
import cn.tinsur.elder.pojo.query.AnnouncementQuery;
import cn.tinsur.elder.service.IAnnouncementService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前台手机端公告 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
@RestController
@RequestMapping("/app/announcements")
public class AppAnnouncementController {
    @Autowired
    private IAnnouncementService announcementService;

    /**
     * 分页查询已发布的公告列表（首页通知条取最新一条、弹层取前4条、公告列表页分页加载，均走本接口）
     * GET /announcements?page=1&limit=4
     */
    @GetMapping
    public Result<IPage<Announcement>> list(AnnouncementQuery announcementQuery) {
        // 前台只展示已发布的公告
        announcementQuery.setStatus(1);
        // 分页参数缺省时取第一页4条（首页通知条与弹层只需要最新4条）
        if (announcementQuery.getPage() == null) {
            announcementQuery.setPage(1);
        }
        if (announcementQuery.getLimit() == null) {
            announcementQuery.setLimit(4);
        }
        return Result.ok(announcementService.list(announcementQuery));
    }

    /**
     * 根据ID查询公告详情（下架或不存在时提示）
     * GET /announcements/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null || announcement.getStatus() == null || announcement.getStatus() != 1) {
            return Result.error("该公告不存在或已下架");
        }
        return Result.ok(announcement);
    }
}
