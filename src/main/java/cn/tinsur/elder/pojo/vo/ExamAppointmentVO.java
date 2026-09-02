package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.ExamAppointment;
import lombok.Data;

@Data
public class ExamAppointmentVO extends ExamAppointment {
    /**
     * 老人姓名（联表查询后填充，便于前端展示）
     */
    private String elderName;

    /**
     * 体检套餐名称（联表查询后填充，便于前端展示）
     */
    private String packageName;
}