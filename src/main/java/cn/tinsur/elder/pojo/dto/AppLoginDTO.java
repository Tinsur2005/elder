package cn.tinsur.elder.pojo.dto;

import lombok.Data;

@Data
public class AppLoginDTO {
    private String name;
    private String password;
    private String userType;
}