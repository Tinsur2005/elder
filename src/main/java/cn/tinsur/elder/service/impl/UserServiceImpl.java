package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<User> list(UserQuery userQuery) {
        IPage<User> page = new Page<>(userQuery.getPage(), userQuery.getLimit());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!ObjectUtils.isEmpty(userQuery.getName()),User::getName,userQuery.getName())
                .like(!ObjectUtils.isEmpty(userQuery.getEmail()), User::getEmail, userQuery.getEmail())
                .between(!ObjectUtils.isEmpty(userQuery.getBeginCreateTime())
                        && !ObjectUtils.isEmpty(userQuery.getEndCreateTime()),
                        User::getCreateTime, userQuery.getBeginCreateTime(),
                        userQuery.getEndCreateTime())
                .orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, lambdaQueryWrapper);
    }
}
