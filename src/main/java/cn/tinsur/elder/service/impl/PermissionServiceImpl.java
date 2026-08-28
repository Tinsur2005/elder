package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.pojo.entity.Permission;
import cn.tinsur.elder.mapper.PermissionMapper;
import cn.tinsur.elder.pojo.vo.PermissionVO;
import cn.tinsur.elder.service.IPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<PermissionVO> selectPermissionTree() {
        //1.查找所有的权限、按sort字段排序
        LambdaQueryWrapper<Permission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Permission::getSort);
        List<Permission> permissionList = permissionMapper.selectList(lambdaQueryWrapper);

        //2.把Permission对象转换为PermissionVO对象，便于插入ChildrenList
        List<PermissionVO> permissionVOList = permissionList.stream().map(permission -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(permission, permissionVO);
            return permissionVO;
        }).toList();

        //3.构建权限树形结构
            //所有一级权限
        List<PermissionVO> permissionVOTree = permissionVOList.stream()
                .filter(permissionVO -> permissionVO.getParentId() == 0)
                .map( permissionVO -> {
                    //构建children
                    permissionVO.setChildren(buildChildrenTree(permissionVO, permissionVOList));
                    return permissionVO;
                })
                .toList();
        return permissionVOTree;
    }

    /**
     * 构建子节点树
     * @param parentPermissionVO 父节点
     * @param permissionVOList 子节点
     * @return
     */
    private List<PermissionVO> buildChildrenTree(PermissionVO parentPermissionVO, List<PermissionVO> permissionVOList){
        return permissionVOList.stream()
                .filter(permissionVO -> permissionVO.getParentId() == parentPermissionVO.getId())
                .map(permissionVO -> {
                    permissionVO.setChildren(buildChildrenTree(permissionVO, permissionVOList)); //继续递归children
                    return permissionVO;
                })
                .toList();
    }
}
