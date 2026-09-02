package cn.tinsur.elder.controller;


import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.service.ITagService;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 老人-标签关联表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-26
 */
@RestController
@RequestMapping("/elder-tags")
public class ElderTagController {
    @Autowired
    private ITagService tagService;

    @Autowired
    private IElderService elderService;

    /**
     * 根据老人id获取老人的标签和所有标签
     * 供前端查询和修改老人Tag的对话框使用
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<Map<String, Object>> getElderTagsAndAllTags(@PathVariable Long id){
        Map<String, Object> map = new HashMap();
        map.put("allTags", tagService.list());
        map.put("elderTags", elderService.getTagsById(id));
        return Result.ok(map);
    }

}

