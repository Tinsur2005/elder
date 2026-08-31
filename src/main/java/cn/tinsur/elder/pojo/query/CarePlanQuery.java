package cn.tinsur.elder.pojo.query;

import lombok.Data;

import java.util.Date;

@Data
public class CarePlanQuery {
    private String name;
    private Long elderId;
    private Integer status;
    private Date beginCreateTime;
    private Date endCreateTime;
    private Integer page;
    private Integer limit;
}