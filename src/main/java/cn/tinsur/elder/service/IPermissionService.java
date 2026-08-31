package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.Permission;
import cn.tinsur.elder.pojo.vo.PermissionVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
public interface IPermissionService extends IService<Permission> {
    List<PermissionVO> selectPermissionTree();

    List<PermissionVO> buildTree(List<PermissionVO> permissionVOList);
}
