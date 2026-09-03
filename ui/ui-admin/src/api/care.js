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

const careItemApi = {
    // 分页查询护理项目列表
    list(careItemQuery) {
        return request.get("/care-items", {params: careItemQuery});
    },
    // 根据ID查询护理项目
    selectById(id) {
        return request.get(`/care-items/${id}`);
    },
    // 获取全部启用状态的护理项目列表List（供护理计划等"选护理项目"下拉框使用）
    listAll() {
        return request.get(`/care-items/list`)
    },
    // 新增护理项目
    add(careItem) {
        return request.post("/care-items", careItem)
    },
    // 修改护理项目
    update(id, careItem) {
        return request.put(`/care-items/${id}`, careItem)
    },
    // 根据ID删除护理项目
    deleteById(id) {
        return request.delete(`/care-items/${id}`);
    },
    // 批量删除护理项目
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/care-items", {data: ids});
    }
}
export default careItemApi