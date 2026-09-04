/*
 *
 *  * ============================================================
 *  *
 *  *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *  *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *  *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *  *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *  *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *  *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *  *
 *  *  项目名称 : 智慧社区养老系统
 *  *  源码作者 : Tinsur (tinsur.cn)
 *  *  作者主页 : https://www.tinsur.cn
 *  *  联系方式 : me@tinsur.cn
 *  *  开源协议 : GPL 3.0
 *  *
 *  * ============================================================
 *
 */

package cn.tinsur.elder.tools;

import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.query.CarePlanQuery;
import cn.tinsur.elder.pojo.query.ExamAppointmentQuery;
import cn.tinsur.elder.pojo.vo.CarePlanVO;
import cn.tinsur.elder.pojo.vo.ExamAppointmentItemVO;
import cn.tinsur.elder.pojo.vo.ExamAppointmentVO;
import cn.tinsur.elder.service.ICarePlanService;
import cn.tinsur.elder.service.IExamAppointmentService;
import cn.tinsur.elder.service.IElderService;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

/**
 * AI 工具（Tool Calling / Function Calling）示例
 *
 * 工作机制：
 * 1. @Tool 注解的方法会被 Spring AI 扫描，把方法名和 description 告诉大模型
 * 2. 大模型根据用户问题自主判断：需不需要调工具、传什么参数
 * 3. 框架通过反射调用对应方法，把返回值转成 JSON 回传给模型
 * 4. 模型拿到数据后，再组织成自然语言回答用户
 *
 * 整个过程对用户透明：用户只问“我今年多大”，模型自己去查库再回答。
 *
 * 使用方式：chatClient.prompt().tools(new ElderTools(elderId, elderService, examAppointmentService, carePlanService))
 */
public class ElderTools {

    private Integer elderId;
    private IElderService elderService;
    private IExamAppointmentService examAppointmentService;
    private ICarePlanService carePlanService;

    public ElderTools(Integer elderId, IElderService elderService, IExamAppointmentService examAppointmentService,
                      ICarePlanService carePlanService) {
        this.elderId = elderId;
        this.elderService = elderService;
        this.examAppointmentService = examAppointmentService;
        this.carePlanService = carePlanService;
    }

    /**
     * 查询当前老人自己的档案信息。
     * 当用户问“我叫什么名字”“我今年多大”“我的手机号是多少”时，
     * 模型会自动调用这个方法，而不是凭空编造答案。
     * 返回值会被序列化成 JSON 回传给模型。
     */
    @Tool(description = "查询当前老人自己的档案信息，包括姓名、年龄、手机号、出生日期、家庭住址。" +
            "gender字段含义：0表示女、1表示男，回答性别相关问题时根据该字段判断，不要凭姓名猜测")
    public Elder getElderInfo() {
        return elderService.getById(elderId);
    }

    /**
     * 查询当前老人的全部体检记录，包括每次体检的日期、套餐名称、状态，
     * 以及每个体检项目的名称、结果（数值或文本）、是否异常、参考范围。
     * 当用户问“我的体检结果怎么样”“上次体检有没有异常”等问题时，
     * 模型会自动调用这个方法查库，而不是凭空编造答案。
     * 返回值会被序列化成 JSON 回传给模型。
     */
    @Tool(description = "查询当前老人的所有体检记录和每项体检结果。" +
            "体检记录的status含义：0待体检、1体检中、2已完成、3已取消、4已过期，AI回答时应重点关注status为2的已完成记录；" +
            "项目明细的status含义：0待检查、1正常、2异常、3未完成，abnormal为1表示该项目结果异常")
    public List<ExamAppointmentVO> getExamRecords() {
        // 1.查询该老人的体检预约列表（复用体检预约的分页查询，一次取100条足够AI回答使用）
        ExamAppointmentQuery examAppointmentQuery = new ExamAppointmentQuery();
        examAppointmentQuery.setElderId(elderId.longValue());
        examAppointmentQuery.setPage(1);
        examAppointmentQuery.setLimit(100);
        List<ExamAppointmentVO> examAppointmentList = examAppointmentService.list(examAppointmentQuery).getRecords();

        // 2.给每条体检记录附上项目明细（含结果数值/文本和参考范围）
        examAppointmentList.forEach(examAppointment -> {
            List<ExamAppointmentItemVO> items = examAppointmentService
                    .getAppointmentItemsById(examAppointment.getId()).getData();
            examAppointment.setItems(items);
        });
        return examAppointmentList;
    }

    /**
     * 查询当前老人的全部护理计划，包括计划名称、护理等级、负责护理人员、起止日期，
     * 以及计划里每个护理项目的执行周期、执行时间和执行日。
     * 当用户问“我的护理计划是什么”“我每天要做哪些护理”等问题时，
     * 模型会自动调用这个方法查库，而不是凭空编造答案。
     * 返回值会被序列化成 JSON 回传给模型。
     */
    @Tool(description = "查询当前老人的所有护理计划和每个计划包含的护理项目。" +
            "计划的status含义：0已结束、1进行中；" +
            "项目的executeCycle含义：0每天、1每周、2每月，executeDay为执行日：周期为每周时存周几（1到7，1代表周一），周期为每月时存几号（1到31），每天时为空")
    public List<CarePlanVO> getCarePlans() {
        // 1.复用护理计划的分页查询，按当前老人id过滤，一次取100条足够AI回答使用
        CarePlanQuery carePlanQuery = new CarePlanQuery();
        carePlanQuery.setElderId(elderId.longValue());
        carePlanQuery.setPage(1);
        carePlanQuery.setLimit(100);
        List<CarePlanVO> carePlanList = carePlanService.list(carePlanQuery).getRecords();

        // 2.给每条护理计划附上护理项目明细（含执行周期和执行时间）
        carePlanList.forEach(carePlan -> {
            carePlan.setItems(carePlanService.getCareItemsById(carePlan.getId()).getData());
        });
        return carePlanList;
    }
}