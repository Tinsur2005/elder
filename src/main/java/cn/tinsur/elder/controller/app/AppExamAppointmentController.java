package cn.tinsur.elder.controller.app;


import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.ExamAppointment;
import cn.tinsur.elder.pojo.entity.ExamPackage;
import cn.tinsur.elder.pojo.query.ExamAppointmentQuery;
import cn.tinsur.elder.pojo.vo.ExamAppointmentItemVO;
import cn.tinsur.elder.pojo.vo.ExamAppointmentVO;
import cn.tinsur.elder.service.IExamAppointmentService;
import cn.tinsur.elder.service.IExamPackageService;
import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.util.AppAuthHelper;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前台手机端体检预约 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@RestController
@RequestMapping("/app/exam-appointments")
public class AppExamAppointmentController {
    @Autowired
    private IExamAppointmentService examAppointmentService;

    @Autowired
    private IExamPackageService examPackageService;

    @Autowired
    private IElderService elderService;

    @Autowired
    private AppAuthHelper appAuthHelper;

    /**
     * 分页查询老人的体检预约列表（老人查自己的，家属查绑定老人的）
     * GET /exam-appointments?elderId=1&status=0
     */
    @GetMapping
    public Result list(ExamAppointmentQuery examAppointmentQuery,
                       @RequestHeader(name = "Authorization") String token) {
        //校验归属：只能查询自己的（或绑定老人的）预约
        Result checkResult = appAuthHelper.checkElderPermission(token, examAppointmentQuery.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //前台手机端列表一次加载，不传分页参数时默认取第一页100条（后续如需分页再扩展）
        if (ObjectUtils.isEmpty(examAppointmentQuery.getPage())) examAppointmentQuery.setPage(1);
        if (ObjectUtils.isEmpty(examAppointmentQuery.getLimit())) examAppointmentQuery.setLimit(100);
        //复用后台分页查询逻辑
        return Result.ok(examAppointmentService.list(examAppointmentQuery));
    }

    /**
     * 根据ID查询体检预约详情（带老人姓名、套餐名称）
     * GET /exam-appointments/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id,
                          @RequestHeader(name = "Authorization") String token) {
        ExamAppointment examAppointment = examAppointmentService.getById(id);
        if (examAppointment == null) {
            return Result.error("该体检预约不存在");
        }
        //校验归属：只能查看自己的（或绑定老人的）预约
        Result checkResult = appAuthHelper.checkElderPermission(token, examAppointment.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //组装带老人姓名、套餐名称的详情VO
        ExamAppointmentVO examAppointmentVO = new ExamAppointmentVO();
        BeanUtils.copyProperties(examAppointment, examAppointmentVO);
        Elder elder = elderService.getById(examAppointment.getElderId());
        if (elder != null) examAppointmentVO.setElderName(elder.getRealName());
        ExamPackage examPackage = examPackageService.getById(examAppointment.getPackageId());
        if (examPackage != null) examAppointmentVO.setPackageName(examPackage.getName());
        return Result.ok(examAppointmentVO);
    }

    /**
     * 新增体检预约（快照套餐价格，并把套餐下的体检项目复制为体检记录明细）
     * POST /exam-appointments
     */
    @PostMapping
    public Result add(@RequestBody ExamAppointment examAppointment,
                      @RequestHeader(name = "Authorization") String token) {
        //校验归属：只能给自己的（或绑定老人的）预约体检
        Result checkResult = appAuthHelper.checkElderPermission(token, examAppointment.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //复用后台新增逻辑（上架校验、价格快照、明细复制）
        return examAppointmentService.addAppointment(examAppointment);
    }

    /**
     * 取消预约（待体检/体检中 → 已取消）
     * PUT /exam-appointments/cancel/1
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id,
                         @RequestHeader(name = "Authorization") String token) {
        //先查出原预约做归属校验
        ExamAppointment examAppointment = examAppointmentService.getById(id);
        if (examAppointment == null) {
            return Result.error("该体检预约不存在");
        }
        Result checkResult = appAuthHelper.checkElderPermission(token, examAppointment.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //复用后台取消逻辑
        return examAppointmentService.cancelAppointment(id);
    }

    /**
     * 获取指定体检记录包含的所有明细（附上参考范围，供前台查看体检结果）
     * GET /exam-appointments/getAppointmentItemsById/1
     */
    @GetMapping("/getAppointmentItemsById/{id}")
    public Result getAppointmentItemsById(@PathVariable Long id,
                                          @RequestHeader(name = "Authorization") String token) {
        //经父预约做归属校验
        ExamAppointment examAppointment = examAppointmentService.getById(id);
        if (examAppointment == null) {
            return Result.error("该体检预约不存在");
        }
        Result checkResult = appAuthHelper.checkElderPermission(token, examAppointment.getElderId());
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        //复用后台明细查询逻辑
        return examAppointmentService.getAppointmentItemsById(id);
    }
}