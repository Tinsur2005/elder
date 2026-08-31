package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.CareTask;
import lombok.Data;

@Data
public class CareTaskVO extends CareTask {
    /**
     * 老人姓名（联表查询后填充，便于前端展示）
     */
    private String elderName;

    /**
     * 执行护理员姓名（联表查询后填充，便于前端展示）
     */
    private String userName;
}