package cn.tinsur.elder.controller.admin;

import cn.tinsur.elder.pojo.vo.DashboardVO;
import cn.tinsur.elder.service.IDashboardService;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 首页看板 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    /**
     * 获取首页看板数据（统计卡片数字 + 各图表数据）
     * GET /dashboard
     */
    @GetMapping
    public Result<DashboardVO> getDashboard() {
        return Result.ok(dashboardService.getDashboard());
    }
}