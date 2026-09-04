/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
package cn.tinsur.elder.service;

import cn.tinsur.elder.pojo.entity.EmailCode;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 邮箱验证码表 服务类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
public interface IEmailCodeService extends IService<EmailCode> {

    /** 场景：绑定邮箱（验证码发到待绑定的新邮箱） */
    String SCENE_BIND_EMAIL = "BIND_EMAIL";

    /** 场景：通过邮箱验证码修改密码（验证码发到当前绑定的邮箱） */
    String SCENE_CHANGE_PASSWORD = "CHANGE_PASSWORD";

    /** 场景：更换绑定邮箱（验证码发到当前绑定的旧邮箱） */
    String SCENE_CHANGE_EMAIL = "CHANGE_EMAIL";

    /** 场景：更换绑定邮箱时验证新邮箱（验证码发到待更换的新邮箱） */
    String SCENE_CHANGE_EMAIL_NEW = "CHANGE_EMAIL_NEW";

    /**
     * 向指定邮箱发送验证码
     * 同邮箱同场景60秒内只允许发送一次，发送前会把旧的未使用验证码全部作废
     * @param email 接收验证码的邮箱
     * @param scene 使用场景
     * @return 发送结果
     */
    Result sendCode(String email, String scene);

    /**
     * 校验用户填写的验证码
     * 校验通过后该验证码立即作废，只能使用一次
     * @param email 接收验证码的邮箱
     * @param scene 使用场景
     * @param code  用户填写的验证码
     * @return 校验通过返回Result.ok()，失败返回带原因的Result.error()
     */
    Result verifyCode(String email, String scene, String code);

    /**
     * 校验用户填写的验证码但不作废
     * 供分步验证流程使用，例如更换邮箱第一步先校验旧邮箱验证码，最后一步完成时再由业务接口调用verifyCode作废
     * @param email 接收验证码的邮箱
     * @param scene 使用场景
     * @param code  用户填写的验证码
     * @return 校验通过返回Result.ok()，失败返回带原因的Result.error()
     */
    Result checkCode(String email, String scene, String code);
}