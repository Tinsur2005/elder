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

import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.pojo.vo.UserVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
public interface IUserService extends IService<User> {

    IPage<UserVO> list(UserQuery userQuery);

    void exportExcel(HttpServletResponse response);

    void importExcel(MultipartFile file);

    Result add(User user);

    /**
     * 根据id获取角色列表
     * @param id
     * @return
     */
    Result<List<Role>> getRolesById(Long id);

    /**
     * 根据用户id删除这个用户的所有角色
     * @param id
     */
    void deleteAllRolesById(Long id);

    /**
     * 根据老人id添加标签，传入的第二个参数应该是标签ID组成的Long数组
     * @param id,roleId
     */
    void addRoleById(Long id, Long[] rowId);

    Result updateRoles(Long id, Long[] roleIds);

    Map<String, Object> selectPermissionByUserId(Long id);

    /**
     * 按角色搜索用户（供护理计划等"选护理人员"远程下拉框使用）
     * @param roleId 角色id（护工=3）
     * @param name 可选的姓名关键字，为空则返回该角色下全部用户
     * @return 用户的List列表
     */
    List<User> searchByRole(Long roleId, String name);

    /**
     * 按姓名搜索所有用户（不做任何角色限定，供护理计划等"选护理人员"远程下拉框使用，
     * 这样即使删掉"护工"角色也不影响绑定人员）
     * @param name 可选的姓名关键字，为空则返回全部用户
     * @return 用户的List列表
     */
    List<User> searchByName(String name);
}
