package cn.tinsur.elder.pojo.query;

import lombok.Data;

import java.util.Date;

@Data
public class ExamAppointmentQuery {
    private Long elderId;
    private String packageName;
    private Integer status;
    private Date beginAppointmentDate;
    private Date endAppointmentDate;
    private Integer page;
    private Integer limit;
}