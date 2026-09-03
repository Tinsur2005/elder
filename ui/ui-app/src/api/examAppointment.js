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

const examAppointmentApi = {
    // 查询指定老人的体检预约列表（附带 elderName、packageName 供展示）
    // 手机端列表一次加载，不传分页参数时后端默认取第一页100条（后续如需分页可加 van-list）
    list(examAppointmentQuery) {
        return request.get("/exam-appointments", {params: examAppointmentQuery})
    },
    // 根据ID查询体检预约
    selectById(id) {
        return request.get(`/exam-appointments/${id}`)
    },
    // 新增体检预约（后端快照套餐价格，预约状态默认待体检）
    add(examAppointment) {
        return request.post("/exam-appointments", examAppointment)
    },
    // 取消预约（待体检/体检中 → 已取消）
    cancel(id) {
        return request.put(`/exam-appointments/cancel/${id}`)
    },
    // 获取指定体检记录包含的全部明细（附上参考范围，供结果展示）
    getAppointmentItemsById(id) {
        return request.get(`/exam-appointments/getAppointmentItemsById/${id}`)
    }
}
export default examAppointmentApi