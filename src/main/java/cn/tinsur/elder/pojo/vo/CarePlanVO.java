package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.CarePlan;
import lombok.Data;

@Data
public class CarePlanVO extends CarePlan {
    /**
     * 绑定的老人姓名（联表查询后填充，便于前端展示）
     */
    private String elderName;

    /**
     * 护理人员姓名（联表查询后填充，便于前端展示）
     */
    private String userName;

    /**
     * 护理等级名称（联表查询后填充，便于前端展示）
     */
    private String careLevelName;
}