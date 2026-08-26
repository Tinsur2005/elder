package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.ElderQuery;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
public interface IElderService extends IService<Elder> {

    IPage<Elder> list(ElderQuery elderrQuery);

    /**
     * 根据老人id获取这个老人所有的标签，返回一个由Tag对象组成的List列表
     * @param id
     * @return
     */
    Result<List<Tag>> getTagsById(Long id);

    /**
     * 根据老人id删除这个老人的所有标签
     * @param id
     */
    void deleteAllTagsById(Long id);

    /**
     * 根据老人id添加标签，传入的第二个参数应该是标签ID组成的Long数组
     * @param id
     * @param tagId
     */
    void addTagById(Long id, Long[] tagId);

    Result updateTags(Long id, Long[] tags);
}
