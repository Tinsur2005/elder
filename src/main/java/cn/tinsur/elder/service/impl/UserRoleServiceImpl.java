package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.pojo.entity.UserRole;
import cn.tinsur.elder.mapper.UserRoleMapper;
import cn.tinsur.elder.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工-角色关联表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-27
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}
