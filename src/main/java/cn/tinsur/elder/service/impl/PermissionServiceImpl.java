package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.pojo.entity.Permission;
import cn.tinsur.elder.mapper.PermissionMapper;
import cn.tinsur.elder.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}
