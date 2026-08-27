package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.UserQuery;
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
public interface IUserService extends IService<User> {

    IPage<User> list(UserQuery userQuery);

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
}
