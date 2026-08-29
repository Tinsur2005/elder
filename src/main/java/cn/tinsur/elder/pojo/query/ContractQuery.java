package cn.tinsur.elder.pojo.query;

import lombok.Data;

import java.util.Date;

@Data
public class ContractQuery {
    private Date beginCreateTime;
    private Date endCreateTime;
    private Integer page;
    private Integer limit;
}