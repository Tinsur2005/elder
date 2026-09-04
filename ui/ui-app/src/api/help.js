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
import request from '@/utils/request.js'

const helpApi = {
    // 查询指定老人的求助列表（附带 elderName、handlerName 供展示）
    // 手机端列表一次加载，不传分页参数时后端默认取第一页100条（后续如需分页可加 van-list）
    list(helpQuery) {
        return request.get("/help-requests", {params: helpQuery})
    },
    // 发起求助（后端默认置为未处理，老人为自己发起、家属代当前绑定老人发起）
    add(helpRequest) {
        return request.post("/help-requests", helpRequest)
    }
}
export default helpApi
