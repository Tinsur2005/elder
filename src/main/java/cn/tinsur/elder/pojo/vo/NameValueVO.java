package cn.tinsur.elder.pojo.vo;

import lombok.Data;

/**
 * 图表通用数据对（名称+数值）
 * 饼图/柱状图都只需要"名称+数值"，统一用这一个VO，避免每个图表都单独建类
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@Data
public class NameValueVO {

    /**
     * 名称（如：待执行、服务合同、独居）
     */
    private String name;

    /**
     * 数值
     */
    private Long value;

    public NameValueVO(String name, Long value) {
        this.name = name;
        this.value = value;
    }
}