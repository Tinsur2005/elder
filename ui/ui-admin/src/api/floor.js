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

const floorApi = {
    // 分页查询楼层列表（可按楼栋筛选）
    list(floorQuery) {
        return request.get("/floors", {params: floorQuery});
    },
    // 根据ID查询楼层
    selectById(id) {
        return request.get(`/floors/${id}`);
    },
    // 获取全部楼层列表List（供房间、床位等页面的楼层下拉框使用，前端按楼栋过滤）
    listAll() {
        return request.get(`/floors/list`)
    },
    // 新增楼层
    add(floor) {
        return request.post("/floors", floor)
    },
    // 修改楼层
    update(id, floor) {
        return request.put(`/floors/${id}`, floor)
    },
    // 根据ID删除楼层（名下有房间时不允许删除）
    deleteById(id) {
        return request.delete(`/floors/${id}`);
    },
    // 批量删除楼层
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/floors", {data: ids});
    }
}
export default floorApi
