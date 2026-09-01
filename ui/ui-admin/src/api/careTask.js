import request from "@/utils/request.js";

const careTaskApi = {
    // 分页查询护理任务列表
    list(careTaskQuery) {
        return request.get("/care-task", {params: careTaskQuery});
    },
    // 根据ID查询护理任务详情
    selectById(id) {
        return request.get(`/care-task/${id}`);
    },
    // 完成任务（记录执行结果、打卡照片、备注、执行人）
    complete(careTask) {
        return request.put("/care-task/complete", careTask)
    },
    // 跳过/取消任务
    skip(id) {
        return request.put(`/care-task/skip/${id}`)
    },
    // 根据ID删除任务
    deleteById(id) {
        return request.delete(`/care-task/${id}`)
    },
    // 批量删除任务（axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里）
    deleteAll(ids) {
        return request.delete("/care-task", {data: ids});
    }
}
export default careTaskApi