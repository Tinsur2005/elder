package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.Contract;
import lombok.Data;

@Data
public class ContractVO extends Contract {
    /**
     * 绑定的老人姓名（联表查询后填充，便于前端展示）
     */
    private String elderName;
}