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
import cn.tinsur.elder.pojo.query.ExamAppointmentQuery;
import cn.tinsur.elder.pojo.vo.ExamAppointmentItemVO;
import cn.tinsur.elder.pojo.vo.ExamAppointmentVO;
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
 * 使用方式：chatClient.prompt().tools(new ElderTools(elderId, elderService, examAppointmentService))
 */
public class ElderTools {

    private Integer elderId;
    private IElderService elderService;
    private IExamAppointmentService examAppointmentService;

    public ElderTools(Integer elderId, IElderService elderService, IExamAppointmentService examAppointmentService) {
        this.elderId = elderId;
        this.elderService = elderService;
        this.examAppointmentService = examAppointmentService;
    }

    /**
     * 查询当前老人自己的档案信息。
     * 当用户问“我叫什么名字”“我今年多大”“我的手机号是多少”时，
     * 模型会自动调用这个方法，而不是凭空编造答案。
     * 返回值会被序列化成 JSON 回传给模型。
     */
    @Tool(description = "查询当前老人自己的档案信息，包括姓名、年龄、手机号、出生日期、家庭住址")
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


}