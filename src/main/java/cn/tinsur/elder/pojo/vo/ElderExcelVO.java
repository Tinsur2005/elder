/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
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
    @ExcelProperty(value = "用户名")
    private String name;

    /**
     * 密码哈希
     */
    @ExcelProperty(value = "密码哈希")
    private String password;

    @ExcelProperty(value = "姓名")
    private String realName;
    /**
     * 头像URL
     */
    @ExcelProperty(value = "头像URL")
    private String avatar;

    /**
     * 性别（0：女，1：男）
     */
    @ExcelProperty(value = "性别（0：女，1：男）")
    private Integer gender;

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