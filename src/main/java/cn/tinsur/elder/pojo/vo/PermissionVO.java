package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.Permission;
import lombok.Data;

import java.util.List;

@Data
public class PermissionVO extends Permission {
    private List<PermissionVO> children;
}
