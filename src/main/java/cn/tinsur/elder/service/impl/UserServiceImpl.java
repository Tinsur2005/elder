package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.exception.ServiceException;
import cn.tinsur.elder.listener.UserExcelListener;
import cn.tinsur.elder.mapper.UserRoleMapper;
import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.pojo.vo.UserExcelVO;
import cn.tinsur.elder.pojo.vo.UserRole;
import cn.tinsur.elder.service.IUserService;
import cn.tinsur.elder.util.ExcelUtil;
import cn.tinsur.elder.util.Result;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @Autowired
    private UserRoleMapper userRoleMapper;

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

    /**
     * 导出用户信息
     * @param response
     */
    @Override
    public void exportExcel(HttpServletResponse response) {
        List<User> list = userMapper.selectList(null); //写null则查出所有用户
        List<UserExcelVO> userExcelVOList = list.stream().map(user -> {
            UserExcelVO userExcelVO = new UserExcelVO();
            BeanUtils.copyProperties(user, userExcelVO);
            return userExcelVO;
        }).toList();
        ExcelUtil.exportExcel(response, userExcelVOList, UserExcelVO.class, "用户信息表");
    }

    /**
     * 导入用户信息
     * @param file
     */
    @Override
    public void importExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), UserExcelVO.class, new UserExcelListener(userMapper)).sheet().doRead();
        } catch (ExcelDataConvertException e) {
            //单元格类型解析失败，属于格式问题
            throw new ServiceException("导入失败：Excel格式有误，请使用导出的模板文件");
        } catch (DataIntegrityViolationException e) {
            //数据写入数据库时发生约束冲突，属于数据冲突
            throw new ServiceException("导入失败：数据冲突，存在重复或不符合字段要求的数据");
        } catch (Exception e) {
            throw new ServiceException("导入失败，请检查文件内容后重试");
        }

    }

    /**
     * 判断用户是否存在
     */
    public Boolean isExists(@RequestParam String name) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("name", name));
        return user != null;
    }

    @Override
    public Result add(User user) {
        if(isExists(user.getName())) {
            throw new ServiceException("用户名已存在，换一个用户名试试吧");
        }
        userMapper.insert(user);
        return Result.ok("新增成功");
    }

    @Override
    public Result<List<Role>> getRolesById(Long id) {
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole :: getUserId, id);
        List<Long> list =
                userRoleMapper.selectList(lambdaQueryWrapper)
                        .stream()
                        .map(UserRole :: getRoleId)
                        .toList();
        return Result.ok(list);
    }

    /**
     * 根据用户id删除这个用户的所有角色
     * @param id
     */
    @Override
    public void deleteAllRolesById(Long id) {
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole :: getUserId, id);
        userRoleMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据id添加角色，传入的第二个参数应该是角色ID组成的Long数组
     * @param id
     * @param roleId
     */
    @Override
    public void addRoleById(Long id, Long[] roleId) {
        for (Long role : roleId) {
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(role);
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * 根据id更新角色，传入的第二个参数应该是角色ID组成的Long数组
     * 这个方法的实现方法是，先根据id删除user-role中间表中有关这个用户的所有数据，再根据id和roleId数组插入新的数据
     * @param id
     * @param roles
     * @return
     */
    @Override
    public Result updateRoles(Long id, Long[] roles) {
        deleteAllRolesById(id);
        addRoleById(id, roles);
        return Result.ok("更新成功");
    }
}
