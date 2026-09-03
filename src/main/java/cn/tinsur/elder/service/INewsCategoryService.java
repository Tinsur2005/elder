package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.NewsCategory;
import cn.tinsur.elder.pojo.query.NewsCategoryQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 资讯分类表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-03
 */
public interface INewsCategoryService extends IService<NewsCategory> {

    /**
     * 分页查询资讯分类列表
     * @param newsCategoryQuery 查询条件
     * @return
     */
    IPage<NewsCategory> list(NewsCategoryQuery newsCategoryQuery);

    /**
     * 获取全部启用状态的资讯分类列表List（供资讯编辑时"选分类"下拉框使用）
     * @return
     */
    List<NewsCategory> listAll();

    /**
     * 查询这批分类下的资讯数量（删除分类前校验用）
     * @param ids 分类id集合
     * @return
     */
    Long countNews(List<Long> ids);
}