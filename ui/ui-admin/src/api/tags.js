import request from "@/utils/request.js";

const tagsApi = {
    // 分页查询标签列表
    list(tagQuery) {
        return request.get("/tags", {params: tagQuery});
    },
    // 获取全部标签列表List（供老人标注勾选用）
    listAll() {
        return request.get(`/tags/list`)
    },
    // 根据ID查询标签
    selectById(id) {
        return request.get(`/tags/${id}`);
    },
    // 新增标签
    add(tag) {
        return request.post("/tags", tag)
    },
    // 修改标签
    update(id, tag) {
        return request.put(`/tags/${id}`, tag)
    },
    // 根据ID删除标签
    deleteById(id) {
        return request.delete(`/tags/${id}`);
    },
    // 批量删除标签
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/tags", {data: ids});
    }
}
export default tagsApi