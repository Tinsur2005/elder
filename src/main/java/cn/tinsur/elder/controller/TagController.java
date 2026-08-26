package cn.tinsur.elder.controller;


import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.service.ITagService;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 标签表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-26
 */
@RestController
@RequestMapping("/tags")
public class TagController {
    @Autowired
    private ITagService tagService;

    /**
     * 获取全部标签列表List
     * @return
     */
    @GetMapping("/list")
    public Result<List<Tag>> list() {
        List<Tag> list = tagService.list();
        return Result.ok(list);
    }

}

