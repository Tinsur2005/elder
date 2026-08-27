package cn.tinsur.elder.controller;


import cn.tinsur.elder.mapper.RoleMapper;
import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-27
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 查询所有角色（供用户弹窗勾选角色时展示）
     */
    @GetMapping("/list")
    public Result<List<Role>> list() {
        List<Role> roles = roleMapper.selectList(null); //写null则查出所有角色
        return Result.ok(roles);
    }

}