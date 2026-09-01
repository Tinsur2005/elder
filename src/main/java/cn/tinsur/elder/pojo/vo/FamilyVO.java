package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Family;
import lombok.Data;

import java.util.List;

@Data
public class FamilyVO extends Family {
    // 关联的老人列表
    private List<Elder> elders;
}