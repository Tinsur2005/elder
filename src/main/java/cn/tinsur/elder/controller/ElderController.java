package cn.tinsur.elder.controller;


import cn.tinsur.elder.mapper.ElderMapper;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Tag;
import cn.tinsur.elder.pojo.query.ElderQuery;
import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 老人们信息表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-25
 */
@RestController
@RequestMapping("/elders")
public class ElderController {

    @Autowired
    private IElderService elderService;
    @Autowired
    private ElderMapper elderMapper;

    /**
     * 分页查询老人列表
     * GET /elders?page=1&limit=10&name=xxx&phone=xxx
     */
    @GetMapping
    public Result<IPage<Elder>> list(ElderQuery elderQuery) {
        IPage<Elder> page = elderService.list(elderQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询老人
     * GET /elders/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(elderService.getById(id));
    }

    /**
     * 新增老人
     * POST /elders
     */
    @PostMapping
    public Result add(@RequestBody Elder elder) {
        if(isExists(elder.getName())) {
            return Result.error("已有同名老人存在，请修改姓名后重试");
        }
        elderService.save(elder);
        return Result.ok("新增成功");
    }

    /**
     * 修改老人
     * PUT /elders/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Elder elder) {
        elder.setId(id);
        elderService.updateById(elder);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除老人（逻辑删除）
      * DELETE /elders/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        elderService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除老人
      * DELETE /elders
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        elderService.removeByIds(java.util.Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断老人是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        Elder elder = elderService.getOne(new QueryWrapper<Elder>().eq("name", name));
        return elder != null;
    }

    /**
     * 获取指定老人Tags标注列表，
     * result.dat中存放老人所有的Tag组成的List列表
     */
    @GetMapping("/getTagsById/{id}")
    public Result<List<Tag>> getTagsById(@PathVariable Long id) {
        return elderService.getTagsById(id);
    }

    /**
     * 修改更新老人的Tags标注列表
     */
    @PutMapping("/updateTags/{id}")
    public Result updateTags (@PathVariable Long id, @RequestBody Long[] tags) {
        return elderService.updateTags(id, tags);
    }
}

