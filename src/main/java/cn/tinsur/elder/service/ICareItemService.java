package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.CareItem;
import cn.tinsur.elder.pojo.query.CareItemQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 护理项目表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
public interface ICareItemService extends IService<CareItem> {

    /**
     * 分页查询护理项目列表
     * @param careItemQuery 查询条件（名称、状态、创建时间范围、分页）
     * @return 分页结果
     */
    IPage<CareItem> list(CareItemQuery careItemQuery);

    /**
     * 获取全部启用状态的护理项目列表（供护理计划等"选护理项目"下拉框使用）
     * @return 护理项目的List列表
     */
    List<CareItem> listAll();
}