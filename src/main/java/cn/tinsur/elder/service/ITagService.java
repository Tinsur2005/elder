package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.query.TagQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-26
 */
public interface ITagService extends IService<Tag> {

    /**
     * 分页查询标签列表
     */
    IPage<Tag> list(TagQuery tagQuery);

    Long getCount(Long id);
}
