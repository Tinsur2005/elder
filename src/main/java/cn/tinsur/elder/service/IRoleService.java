package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.query.RoleQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

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

}