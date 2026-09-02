package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.ExamPackage;
import lombok.Data;

@Data
public class ExamPackageVO extends ExamPackage {
    /**
     * 套餐包含的体检项目数量
     */
    private Integer itemCount;
}