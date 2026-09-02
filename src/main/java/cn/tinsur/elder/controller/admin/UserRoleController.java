package cn.tinsur.elder.controller.admin;


import cn.tinsur.elder.service.IRoleService;
import cn.tinsur.elder.service.IUserService;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 员工-角色关联表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-27
 */
@RestController
@RequestMapping("/admin/user-roles")
public class UserRoleController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    /**
     * 根据用户id获取用户角色和所有角色
     * 供前端查询和修改用户角色的对话框使用
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<Map<String, Object>> getUserRolesAndAllRoles(@PathVariable Long id){
        Map<String, Object> map = new HashMap();
        map.put("allRoles", roleService.list());
        map.put("userRoles", userService.getRolesById(id));
        return Result.ok(map);
    }
}

