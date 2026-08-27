package cn.tinsur.elder.pojo.vo;

import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class ElderVO extends Elder {
    // 标签列表
    private List<Tag> tags;
}
