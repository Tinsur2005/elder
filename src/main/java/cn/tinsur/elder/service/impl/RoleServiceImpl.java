package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.mapper.RoleMapper;
import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.query.RoleQuery;
import cn.tinsur.elder.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-27
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public IPage<Role> list(RoleQuery roleQuery) {
        IPage<Role> page = new Page<>(roleQuery.getPage(), roleQuery.getLimit());
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(roleQuery.getName()), Role::getName, roleQuery.getName())
                .like(!ObjectUtils.isEmpty(roleQuery.getCode()), Role::getCode, roleQuery.getCode())
                .between(!ObjectUtils.isEmpty(roleQuery.getBeginCreateTime())
                        && !ObjectUtils.isEmpty(roleQuery.getEndCreateTime()),
                        Role::getCreateTime, roleQuery.getBeginCreateTime(),
                        roleQuery.getEndCreateTime())
                .orderByDesc(Role::getCreateTime);
        return roleMapper.selectPage(page, lambdaQueryWrapper);
    }
}