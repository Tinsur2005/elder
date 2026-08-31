package cn.tinsur.elder.mapper;

import cn.tinsur.elder.pojo.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-28
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> selectPermissionByUserId(Integer id);
}
