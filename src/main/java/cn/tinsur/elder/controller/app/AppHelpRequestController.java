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
package cn.tinsur.elder.controller.app;

import cn.tinsur.elder.pojo.entity.HelpRequest;
import cn.tinsur.elder.pojo.query.HelpRequestQuery;
import cn.tinsur.elder.pojo.vo.HelpRequestVO;
import cn.tinsur.elder.service.IHelpRequestService;
import cn.tinsur.elder.util.AppAuthHelper;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前台手机端求助记录 前端控制器
 * </p>
 * 老人可为自己发起求助，家属可代当前绑定老人发起求助
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@RestController
@RequestMapping("/app/help-requests")
public class AppHelpRequestController {
    @Autowired
    private IHelpRequestService helpRequestService;

    @Autowired
    private AppAuthHelper appAuthHelper;

    /**
     * 分页查询老人的求助列表（老人查自己的，家属查绑定老人的），VO带老人姓名、处理人姓名
     * GET /help-requests?elderId=1
     */
    @GetMapping
    public Result list(HelpRequestQuery helpRequestQuery,
                       @RequestHeader(name = "Authorization") String token) {
        //校验归属：只能查询自己的（或绑定老人的）求助
        Result checkResult = appAuthHelper.checkElderPermission(token, helpRequestQuery.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //前台手机端列表一次加载，不传分页参数时默认取第一页100条（后续如需分页再扩展）
        if (ObjectUtils.isEmpty(helpRequestQuery.getPage())) helpRequestQuery.setPage(1);
        if (ObjectUtils.isEmpty(helpRequestQuery.getLimit())) helpRequestQuery.setLimit(100);
        //复用后台分页查询逻辑
        return Result.ok(helpRequestService.list(helpRequestQuery));
    }

    /**
     * 发起求助（老人为自己发起，家属代当前绑定老人发起）
     * POST /help-requests
     */
    @PostMapping
    public Result add(@RequestBody HelpRequest helpRequest,
                     @RequestHeader(name = "Authorization") String token) {
        //校验归属：只能为自己的（或绑定老人的）发起求助
        Result checkResult = appAuthHelper.checkElderPermission(token, helpRequest.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //求助内容必填，紧急程度和求助类型给默认值，状态默认未处理
        if (ObjectUtils.isEmpty(helpRequest.getContent())) {
            return Result.error("请填写求助内容");
        }
        if (ObjectUtils.isEmpty(helpRequest.getType())) helpRequest.setType(3);
        if (ObjectUtils.isEmpty(helpRequest.getUrgency())) helpRequest.setUrgency(0);
        helpRequest.setStatus(0);
        helpRequestService.save(helpRequest);
        return Result.ok("求助已提交，我们会尽快处理", helpRequest.getId());
    }
}
