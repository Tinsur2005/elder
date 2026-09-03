package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.News;
import cn.tinsur.elder.pojo.query.NewsQuery;
import cn.tinsur.elder.pojo.vo.NewsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 资讯表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
public interface INewsService extends IService<News> {

    /**
     * 分页查询资讯列表，返回 NewsVO（带分类名称）
     * @param newsQuery 查询条件
     * @return
     */
    IPage<NewsVO> list(NewsQuery newsQuery);
}