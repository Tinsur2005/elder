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
package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.ExamPackage;
import cn.tinsur.elder.pojo.entity.ExamPackageItem;
import cn.tinsur.elder.pojo.query.ExamPackageQuery;
import cn.tinsur.elder.pojo.vo.ExamPackageVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 体检套餐表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
public interface IExamPackageService extends IService<ExamPackage> {

    /**
     * 分页查询体检套餐列表，返回带项目数量的ExamPackageVO
     * @param examPackageQuery 查询条件
     * @return 分页结果
     */
    IPage<ExamPackageVO> list(ExamPackageQuery examPackageQuery);

    /**
     * 获取全部上架状态的体检套餐列表，供体检预约选择套餐时使用
     * @return 体检套餐的List列表
     */
    List<ExamPackage> listAll();

    /**
     * 根据id获取该套餐包含的体检项目列表
     * @param id 套餐id
     * @return
     */
    Result<List<ExamPackageItem>> getPackageItemsById(Long id);

    /**
     * 根据套餐id更新该套餐包含的体检项目，先删除该套餐所有项目，再插入新数据
     * @param id
     * @param examPackageItems
     * @return
     */
    Result updatePackageItems(Long id, List<ExamPackageItem> examPackageItems);

    /**
     * 根据套餐id删除体检套餐，级联删除该套餐的全部项目关联
     * @param id 套餐id
     */
    void deletePackageById(Long id);

    /**
     * 批量删除体检套餐，同样级联删除各套餐的全部项目关联
     * @param ids 套餐id集合
     */
    void deletePackageBatch(List<Long> ids);

    /**
     * 统计体检套餐被体检预约引用的数量
     * @param ids 体检套餐ID集合
     * @return 引用数量
     */
    Long countInAppointment(List<Long> ids);
}