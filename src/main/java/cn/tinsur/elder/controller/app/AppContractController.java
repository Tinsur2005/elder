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


import cn.tinsur.elder.pojo.entity.Contract;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.query.ContractQuery;
import cn.tinsur.elder.pojo.vo.ContractVO;
import cn.tinsur.elder.service.IContractService;
import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.util.AppAuthHelper;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前台手机端合同 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@RestController
@RequestMapping("/app/contracts")
public class AppContractController {
    @Autowired
    private IContractService contractService;

    @Autowired
    private IElderService elderService;

    @Autowired
    private AppAuthHelper appAuthHelper;

    /**
     * 分页查询老人的合同列表（老人查自己的，家属查绑定老人的），VO带老人姓名
     * GET /contracts?elderId=1
     */
    @GetMapping
    public Result list(ContractQuery contractQuery,
                       @RequestHeader(name = "Authorization") String token) {
        //校验归属：只能查询自己的（或绑定老人的）合同
        Result checkResult = appAuthHelper.checkElderPermission(token, contractQuery.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //前台手机端列表一次加载，不传分页参数时默认取第一页100条（后续如需分页再扩展）
        if (ObjectUtils.isEmpty(contractQuery.getPage())) contractQuery.setPage(1);
        if (ObjectUtils.isEmpty(contractQuery.getLimit())) contractQuery.setLimit(100);
        //复用后台分页查询逻辑
        IPage<ContractVO> page = contractService.list(contractQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询合同详情（带老人姓名）
     * GET /contracts/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id,
                          @RequestHeader(name = "Authorization") String token) {
        Contract contract = contractService.getById(id);
        if (contract == null) {
            return Result.error("该合同不存在");
        }
        //校验归属：只能查看自己的（或绑定老人的）合同
        Result checkResult = appAuthHelper.checkElderPermission(token, contract.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //组装带老人姓名的详情VO
        ContractVO contractVO = new ContractVO();
        BeanUtils.copyProperties(contract, contractVO);
        Elder elder = elderService.getById(contract.getElderId());
        if (elder != null) contractVO.setElderName(elder.getRealName());
        return Result.ok(contractVO);
    }
}
