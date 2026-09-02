import request from "@/utils/request.js";

const examAppointmentApi = {
    // 分页查询体检预约列表
    list(examAppointmentQuery) {
        return request.get("/exam-appointments", {params: examAppointmentQuery});
    },
    // 根据ID查询体检预约
    selectById(id) {
        return request.get(`/exam-appointments/${id}`);
    },
    // 新增体检预约（快照套餐价格，并把套餐下的体检项目复制为体检记录明细）
    add(examAppointment) {
        return request.post("/exam-appointments", examAppointment)
    },
    // 修改体检预约（仅限待体检状态，换套餐时重建明细）
    update(id, examAppointment) {
        return request.put(`/exam-appointments/${id}`, examAppointment)
    },
    // 开始体检（待体检 → 体检中）
    start(id) {
        return request.put(`/exam-appointments/start/${id}`)
    },
    // 取消预约（待体检/体检中 → 已取消）
    cancel(id) {
        return request.put(`/exam-appointments/cancel/${id}`)
    },
    // 获取指定体检记录包含的全部明细（附上参考范围，供结果录入/展示）
    getAppointmentItemsById(id) {
        return request.get(`/exam-appointments/getAppointmentItemsById/${id}`);
    },
    // 暂存体检结果（先删后插，仅限体检中状态）
    updateAppointmentItems(id, examAppointmentItems) {
        return request.put(`/exam-appointments/updateAppointmentItems/${id}`, examAppointmentItems)
    },
    // 完成体检（保存全部明细结果并流转为已完成，数值型结果自动判定是否异常）
    complete(id, examAppointmentItems) {
        return request.put(`/exam-appointments/complete/${id}`, examAppointmentItems)
    },
    // 根据ID删除体检预约
    deleteById(id) {
        return request.delete(`/exam-appointments/${id}`);
    },
    // 批量删除体检预约
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/exam-appointments", {data: ids});
    }
}
export default examAppointmentApi