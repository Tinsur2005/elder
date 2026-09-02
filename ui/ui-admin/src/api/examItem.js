import request from "@/utils/request.js";

const examItemApi = {
    // 分页查询体检项目列表
    list(examItemQuery) {
        return request.get("/exam-items", {params: examItemQuery});
    },
    // 根据ID查询体检项目
    selectById(id) {
        return request.get(`/exam-items/${id}`);
    },
    // 获取全部启用状态的体检项目列表List（供体检套餐等"选体检项目"下拉框使用）
    listAll() {
        return request.get(`/exam-items/list`)
    },
    // 新增体检项目
    add(examItem) {
        return request.post("/exam-items", examItem)
    },
    // 修改体检项目
    update(id, examItem) {
        return request.put(`/exam-items/${id}`, examItem)
    },
    // 根据ID删除体检项目
    deleteById(id) {
        return request.delete(`/exam-items/${id}`);
    },
    // 批量删除体检项目
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/exam-items", {data: ids});
    }
}
export default examItemApi