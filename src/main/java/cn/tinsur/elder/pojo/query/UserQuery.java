package cn.tinsur.elder.pojo.query;

import lombok.Data;

@Data
public class UserQuery {
    private String name;
    private String email;
    private Integer page;
    private Integer limit;
}
