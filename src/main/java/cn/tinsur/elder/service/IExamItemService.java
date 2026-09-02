package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.ExamItem;
import cn.tinsur.elder.pojo.query.ExamItemQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
/**
 * <p>
 * 体检项目表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
public interface IExamItemService extends IService<ExamItem> {

    /**
     * 分页查询体检项目列表
     * @param examItemQuery 查询条件
     * @return 分页结果
     */
    IPage<ExamItem> list(ExamItemQuery examItemQuery);

    /**
     * 获取全部启用状态的体检项目列表，供体检套餐选择项目时使用
     * @return 体检项目的List列表
     */
    List<ExamItem> listAll();

    /**
     * 判断体检项目是否已被体检套餐引用
     * @param ids 体检项目ID集合
     * @return 引用数量
     */
    Long countInPackage(List<Long> ids);
}