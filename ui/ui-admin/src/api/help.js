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
import request from "@/utils/request.js";

const helpApi = {
    // 分页查询求助列表
    list(helpQuery) {
        return request.get("/help-requests", {params: helpQuery});
    },
    // 提交处理，填写处理结果后将该求助置为已处理
    handle(id, result) {
        return request.put(`/help-requests/handle/${id}`, {result})
    },
    // 忽略求助，将该求助置为已忽略
    ignore(id) {
        return request.put(`/help-requests/ignore/${id}`)
    },
    // 根据ID删除求助
    deleteById(id) {
        return request.delete(`/help-requests/${id}`);
    },
    // 批量删除求助
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/help-requests", {data: ids});
    }
}
export default helpApi