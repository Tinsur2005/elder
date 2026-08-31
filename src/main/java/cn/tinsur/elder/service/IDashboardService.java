package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.vo.DashboardVO;

/**
 * <p>
 * 首页看板 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
public interface IDashboardService {

    /**
     * 获取首页看板数据（统计卡片数字 + 各图表数据）
     * 直接复用各业务模块的Mapper做统计，不写新SQL
     *
     * @return 看板数据
     */
    DashboardVO getDashboard();
}