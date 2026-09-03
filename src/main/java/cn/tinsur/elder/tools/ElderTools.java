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
import cn.tinsur.elder.service.IElderService;
import org.springframework.ai.tool.annotation.Tool;

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
 * 使用方式：chatClient.prompt().tools(new ElderTools(elderId, elderService))
 */
public class ElderTools {

    private Integer elderId;
    private IElderService elderService;

    public ElderTools(Integer elderId, IElderService elderService) {
        this.elderId = elderId;
        this.elderService = elderService;
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
}