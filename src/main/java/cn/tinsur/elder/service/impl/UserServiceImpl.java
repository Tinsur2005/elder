package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.exception.ServiceException;
import cn.tinsur.elder.listener.UserExcelListener;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.pojo.vo.UserExcelVO;
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
}
