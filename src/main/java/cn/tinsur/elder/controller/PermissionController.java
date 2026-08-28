package cn.tinsur.elder.controller;


import cn.tinsur.elder.pojo.vo.PermissionVO;
import cn.tinsur.elder.service.IPermissionService;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

}

