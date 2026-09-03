package cn.tinsur.elder.controller.app;


import cn.tinsur.elder.pojo.query.CareTaskQuery;
import cn.tinsur.elder.pojo.vo.CareTaskVO;
import cn.tinsur.elder.service.ICareTaskService;
import cn.tinsur.elder.util.AppAuthHelper;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前台手机端护理任务 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@RestController
@RequestMapping("/app/care-tasks")
public class AppCareTaskController {
    @Autowired
    private ICareTaskService careTaskService;

    @Autowired
    private AppAuthHelper appAuthHelper;

    /**
     * 分页查询老人的护理任务列表（老人查自己的，家属查绑定老人的），VO带老人姓名、执行护理员姓名
     * GET /care-tasks?elderId=1&status=0
     */
    @GetMapping
    public Result list(CareTaskQuery careTaskQuery,
                       @RequestHeader(name = "Authorization") String token) {
        //校验归属：只能查询自己的（或绑定老人的）护理任务
        Result checkResult = appAuthHelper.checkElderPermission(token, careTaskQuery.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //前台手机端列表一次加载，不传分页参数时默认取第一页100条（后续如需分页再扩展）
        if (ObjectUtils.isEmpty(careTaskQuery.getPage())) careTaskQuery.setPage(1);
        if (ObjectUtils.isEmpty(careTaskQuery.getLimit())) careTaskQuery.setLimit(100);
        //复用按老人查询逻辑（不涉及后台登录用户的权限）
        IPage<CareTaskVO> page = careTaskService.listByElder(careTaskQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询护理任务详情（带老人姓名、执行护理员姓名）
     * GET /care-tasks/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id,
                          @RequestHeader(name = "Authorization") String token) {
        CareTaskVO careTask = careTaskService.getDetail(id);
        if (careTask == null) {
            return Result.error("该护理任务不存在");
        }
        //校验归属：只能查看自己的（或绑定老人的）护理任务
        Result checkResult = appAuthHelper.checkElderPermission(token, careTask.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        return Result.ok(careTask);
    }
}