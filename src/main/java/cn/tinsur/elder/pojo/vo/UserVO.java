package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserVO extends User {
    // 角色列表
    private List<Role> roles;
}