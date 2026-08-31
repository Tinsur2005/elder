package cn.tinsur.elder.controller;


import cn.tinsur.elder.pojo.entity.Permission;
import cn.tinsur.elder.pojo.vo.PermissionVO;
import cn.tinsur.elder.service.IPermissionService;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    @GetMapping("/selectPermissionTree")
    public Result<List<PermissionVO>> selectPermissionTree () {
        return Result.ok(permissionService.selectPermissionTree());
    }

    /**
     * 根据ID查询权限
     * GET /permissions/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(permissionService.getById(id));
    }

    /**
     * 新增权限
     * POST /permissions
     */
    @PostMapping
    public Result add(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.ok("新增成功");
    }

    /**
     * 修改权限
     * PUT /permissions/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        permissionService.updateById(permission);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除权限（逻辑删除）
     * DELETE /permissions/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        permissionService.removeById(id);
        return Result.ok("删除成功");
    }
}

