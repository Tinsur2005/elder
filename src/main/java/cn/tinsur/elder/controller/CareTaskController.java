package cn.tinsur.elder.controller;


import cn.tinsur.elder.pojo.entity.CareTask;
import cn.tinsur.elder.pojo.query.CareTaskQuery;
import cn.tinsur.elder.pojo.vo.CareTaskVO;
import cn.tinsur.elder.service.ICareTaskService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 护理任务与打卡记录表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-31
 */
@RestController
@RequestMapping("/care-task")
public class CareTaskController {

    @Autowired
    private ICareTaskService careTaskService;

    /**
     * 分页查询护理任务列表
     * GET /care-task?page=1&limit=10&elderId=1&status=0&beginPlanExecuteDate=xxx&endPlanExecuteDate=xxx
     */
    @GetMapping
    public Result<IPage<CareTaskVO>> pageList(CareTaskQuery careTaskQuery) {
        IPage<CareTaskVO> page = careTaskService.list(careTaskQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询护理任务详情
     * GET /care-task/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(careTaskService.getDetail(id));
    }

    /**
     * 完成任务：记录实际完成时间、执行结果、打卡照片、备注、执行人
     * PUT /care-task/complete
     */
    @PutMapping("/complete")
    public Result complete(@RequestBody CareTask careTask) {
        return careTaskService.complete(careTask.getId(), careTask);
    }

    /**
     * 跳过/取消任务
     * PUT /care-task/skip/1
     */
    @PutMapping("/skip/{id}")
    public Result skip(@PathVariable Long id) {
        return careTaskService.skip(id);
    }
}