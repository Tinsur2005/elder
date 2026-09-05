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

const bedApi = {
    // 分页查询床位列表（可按楼栋、楼层、房间、状态筛选）
    list(bedQuery) {
        return request.get("/beds", {params: bedQuery});
    },
    // 根据ID查询床位
    selectById(id) {
        return request.get(`/beds/${id}`);
    },
    // 获取全部床位列表List（供入住登记等页面的床位下拉框使用）
    listAll() {
        return request.get(`/beds/list`)
    },
    // 获取全部空闲床位列表List（供入住办理第二步分配床位使用）
    listFree() {
        return request.get(`/beds/free`)
    },
    // 新增床位
    add(bed) {
        return request.post("/beds", bed)
    },
    // 修改床位
    update(id, bed) {
        return request.put(`/beds/${id}`, bed)
    },
    // 根据ID删除床位（已被占用时不允许删除）
    deleteById(id) {
        return request.delete(`/beds/${id}`);
    },
    // 批量删除床位
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/beds", {data: ids});
    }
}
export default bedApi