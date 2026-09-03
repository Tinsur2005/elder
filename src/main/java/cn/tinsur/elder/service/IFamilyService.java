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

import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Family;
import cn.tinsur.elder.pojo.query.FamilyQuery;
import cn.tinsur.elder.pojo.vo.FamilyVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 家属表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-01
 */
public interface IFamilyService extends IService<Family> {

    IPage<FamilyVO> list(FamilyQuery familyQuery);

    /**
     * 根据家属id获取这个家属关联的所有老人，返回一个由Elder对象组成的List列表
     * @param id
     * @return
     */
    Result<List<Elder>> getEldersById(Long id);

    /**
     * 根据家属id删除这个家属的所有老人关联
     * @param id
     */
    void deleteAllEldersById(Long id);

    /**
     * 根据家属id添加老人关联，传入的第二个参数应该是老人ID组成的Long数组
     * @param id
     * @param elderId
     */
    void addElderById(Long id, Long[] elderId);

    /**
     * 根据家属id更新老人关联，传入的第二个参数应该是老人ID组成的Long数组
     * 实现方式：先删除elder-family中间表中该家属的所有关联数据，再插入新的关联数据
     * @param id
     * @param elderIds
     * @return
     */
    Result updateElders(Long id, Long[] elderIds);

    /**
     * 删除家属（逻辑删除），并同步删除elder-family中间表中该家属的所有关联数据
     * @param id
     */
    void deleteFamilyById(Long id);

    /**
     * 批量删除家属（逻辑删除），并同步删除elder-family中间表中这些家属的所有关联数据
     * @param ids
     */
    void deleteFamilyBatch(Long[] ids);

    /**
     * 根据真实姓名（realName）模糊查询家属，供远程搜索下拉框使用
     * 可输入部分姓名，也可输入全部姓名，返回匹配到的家属列表（最多返回20条）
     * @param name 家属真实姓名的关键字（可空，为空则返回最近20个家属）
     * @return
     */
    List<Family> searchByName(String name);
}