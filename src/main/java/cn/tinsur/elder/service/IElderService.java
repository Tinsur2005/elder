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
import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.ElderQuery;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.pojo.vo.ElderVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

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

    IPage<ElderVO> list(ElderQuery elderrQuery);

    /**
     * 根据id获取老人详情（ElderVO，带标签列表tags），供前台手机端个人信息页使用
     * @param id 老人id
     * @return
     */
    ElderVO getVOById(Long id);

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

    void exportExcel(HttpServletResponse response);

    /**
     * 根据真实姓名（realName）模糊查询老人，供合同选老人等"远程搜索"下拉框使用
     * 可输入部分姓名，也可输入全部姓名，返回匹配到的老人列表（最多返回20条）
     * @param name 老人真实姓名的关键字（可空，为空则返回最近20个老人）
     * @return
     */
    List<Elder> searchByName(String name);

    void importExcel(MultipartFile file);
}
