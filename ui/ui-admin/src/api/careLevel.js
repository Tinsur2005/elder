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

const careLevelApi = {
    // 分页查询护理等级列表
    list(careLevelQuery) {
        return request.get("/care-levels", {params: careLevelQuery});
    },
    // 根据ID查询护理等级
    selectById(id) {
        return request.get(`/care-levels/${id}`);
    },
    // 获取全部启用状态的护理等级列表List（供护理计划等"选护理等级"下拉框使用）
    listAll() {
        return request.get(`/care-levels/list`)
    },
    // 新增护理等级
    add(careLevel) {
        return request.post("/care-levels", careLevel)
    },
    // 修改护理等级
    update(id, careLevel) {
        return request.put(`/care-levels/${id}`, careLevel)
    },
    // 根据ID删除护理等级
    deleteById(id) {
        return request.delete(`/care-levels/${id}`);
    },
    // 批量删除护理等级
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/care-levels", {data: ids});
    }
}
export default careLevelApi