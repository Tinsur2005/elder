package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.CarePlanItem;
import lombok.Data;

@Data
public class CarePlanItemVO extends CarePlanItem {
    /**
     * 护理项目名称（查询后填充，便于前台手机端展示）
     */
    private String careItemName;
}