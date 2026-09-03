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
package cn.tinsur.elder.util;

public class Result<T> {
    public static final int ERROR = 0;
    public static final int OK = 1;

    // 当前状态（程序员判断状态）:成功、失败、未登录、没有权限
    // 当前登录是成功还是失败要告诉前台，前台才能知道弹出的提示框用errorMsg、okMsg
    private Integer code;
    // 描述信息（主要是给用户看的提示信息）
    private String msg;
    // 后台返回给前端的数据 Object， User、List<User>、Map、List<BanjiCountVO>
    private T data;


    public Result() {
    }

    public Result(Integer code) {
        this.code = code;
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 告诉前台成功：code
    public static Result ok() {
        return new Result(OK);
    }

    // 告诉前台成功：code、msg
    public static Result ok(String msg) {
        return new Result(OK, msg);
    }

    // 告诉前台成功：code、data
    public static Result ok(Object data) {
        return new Result(OK, data);
    }

    // 告诉前台成功：code、msg、data
    public static Result ok(String msg, Object data) {
        return new Result(OK, msg, data);
    }


    // 告诉前台成功：code
    public static Result error() {
        return new Result(ERROR);
    }

    // 告诉前台成功：code、msg
    public static Result error(String msg) {
        return new Result(ERROR, msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}