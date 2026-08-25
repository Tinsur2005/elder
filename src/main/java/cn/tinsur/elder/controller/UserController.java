package cn.tinsur.elder.controller;


import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.service.IUserService;
import cn.tinsur.elder.util.JwtUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        //根据用户名查找这个用户
        User dbUser = userService.getOne(new QueryWrapper<User>().eq("name", user.getName()));
        if(dbUser == null) {
            return Result.error("用户名不存在");
        }
        if(!dbUser.getPassword().equals(user.getPassword())) {
            return Result.error("密码错误");
        }
        //登录校验成功，生成Token
        Map<String, Object> map = new HashMap<>();
        map.put("id",dbUser.getId());
        map.put("name", dbUser.getName());
        String token = JwtUtil.creatToken(map);
        return Result.ok("登录成功",token);
    }

    /**
     * 分页查询用户列表
     * GET /users?page=1&limit=10&name=xxx&phone=xxx
     */
    @GetMapping
    public Result<IPage<User>> list(UserQuery userQuery) {
        IPage<User> page = userService.list(userQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询用户
     * GET /users/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /**
     * 新增用户
     * POST /users
     */
    @PostMapping
    public Result add(@RequestBody User user) {
        userService.save(user);
        return Result.ok("新增成功");
    }

    /**
     * 修改用户
     * PUT /users/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateById(user);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除用户（逻辑删除）
     * DELETE /users/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除用户
     * DELETE /users
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        userService.removeByIds(java.util.Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }
}

