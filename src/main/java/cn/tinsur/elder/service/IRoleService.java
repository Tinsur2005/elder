package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.query.RoleQuery;
import cn.tinsur.elder.pojo.vo.PermissionVO;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-27
 */
public interface IRoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     */
    IPage<Role> list(RoleQuery roleQuery);

    List<Long> selectPermissionById(Long id);

    /**
     * 更新角色的权限列表，传入的第二个参数是权限ID组成的Long数组
     * @param id
     * @param permissionIds
     */
    Result updatePermission(Long id, Long[] permissionIds);
}