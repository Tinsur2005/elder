package cn.tinsur.elder.pojo.query;

import lombok.Data;

@Data
public class FamilyQuery {
    private String name;
    private String realName;
    private String phone;
    private String relation;
    private Integer status;
    private Integer page;
    private Integer limit;
}