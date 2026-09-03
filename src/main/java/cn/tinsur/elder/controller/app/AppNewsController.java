package cn.tinsur.elder.controller.app;

import cn.tinsur.elder.pojo.entity.News;
import cn.tinsur.elder.pojo.entity.NewsCategory;
import cn.tinsur.elder.pojo.query.NewsQuery;
import cn.tinsur.elder.pojo.vo.NewsVO;
import cn.tinsur.elder.service.INewsCategoryService;
import cn.tinsur.elder.service.INewsService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资讯前台控制器
 */
@RestController
@RequestMapping("/app/news")
public class AppNewsController {
    @Autowired
    private INewsService newsService;

    @Autowired
    private INewsCategoryService newsCategoryService;

    /**
     * 分页查询已发布的资讯（按发布时间倒序）
     * GET /app/news?page=1&limit=10
     */
    @GetMapping
    public Result<IPage<NewsVO>> list(NewsQuery newsQuery) {
        // 前台只展示已发布的资讯
        newsQuery.setStatus(1);
        if (newsQuery.getPage() == null) { newsQuery.setPage(1); }
        if (newsQuery.getLimit() == null) { newsQuery.setLimit(10); }
        return Result.ok(newsService.list(newsQuery));
    }

    /**
     * 根据ID查询资讯详情，阅读量+1
     * GET /app/news/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        News news = newsService.getById(id);
        if (news == null || news.getStatus() == null || news.getStatus() != 1) {
            return Result.error("该资讯不存在或已下架");
        }
        // 阅读量原子自增，避免并发覆盖
        newsService.update(new LambdaUpdateWrapper<News>().eq(News::getId, id).setSql("views = views + 1"));
        // 组装VO并填充分类名称，阅读量展示自增后的值
        NewsVO newsVO = new NewsVO();
        BeanUtils.copyProperties(news, newsVO);
        newsVO.setViews(news.getViews() == null ? 1 : news.getViews() + 1);
        if (news.getCategoryId() != null) {
            NewsCategory newsCategory = newsCategoryService.getById(news.getCategoryId());
            newsVO.setCategoryName(newsCategory != null ? newsCategory.getName() : null);
        }
        return Result.ok(newsVO);
    }
}