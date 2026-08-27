package cn.tinsur.elder.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ElderExcelVO {

    /**
     * 老人id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名")
    private String name;

    /**
     * 密码哈希
     */
    @ExcelProperty(value = "密码哈希")
    private String password;

    /**
     * 头像URL
     */
    @ExcelProperty(value = "头像URL")
    private String avatar;

    /**
     * 身份证号
     */
    @ExcelProperty(value = "身份证号")
    private String idCardNo;

    /**
     * 状态（0：已停用，1：正常，2：请假，3：退住中，4：入住中，5：已退住）
     */
    @ExcelProperty(value = "状态（0：已停用，1：正常，2：请假，3：退住中，4：入住中，5：已退住）")
    private Integer status;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String phone;

    /**
     * 生日
     */
    @ExcelProperty(value = "生日")
    private Date birthday;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String address;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 逻辑删除（0：未删除，1：已删除）
     */
    @ExcelProperty(value = "逻辑删除（0：未删除，1：已删除）")
    private Integer deleted;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

}