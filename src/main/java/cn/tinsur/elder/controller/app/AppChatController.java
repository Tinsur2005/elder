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

package cn.tinsur.elder.controller.app;

import cn.tinsur.elder.service.IChatService;
import cn.tinsur.elder.util.JwtUtil;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/app/chat")
public class AppChatController {
    @Autowired
    private IChatService chatService;

    @PostMapping("/chat")
    public Result<String> chat(String message, @RequestHeader("Authorization") String token) {
        // 以老人id作为会话id，隔离不同用户的聊天记忆
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer conversationId = (Integer) map.get("id");
        return Result.ok("聊天成功", chatService.chat(message, conversationId));
    }

    //处理流式聊天请求，返回服务器发送事件（SSE）格式的响应流
    @PostMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(String message, @RequestHeader("Authorization") String token) {
        // 以老人id作为会话id，隔离不同用户的聊天记忆
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer conversationId = (Integer) map.get("id");
        return chatService.chatStream(message, conversationId);
    }
}
