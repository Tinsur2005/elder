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

const roomApi = {
    // 分页查询房间列表（可按楼栋、楼层筛选）
    list(roomQuery) {
        return request.get("/rooms", {params: roomQuery});
    },
    // 根据ID查询房间
    selectById(id) {
        return request.get(`/rooms/${id}`);
    },
    // 获取全部房间列表List（供床位页面的房间下拉框使用，前端按楼层过滤）
    listAll() {
        return request.get(`/rooms/list`)
    },
    // 新增房间
    add(room) {
        return request.post("/rooms", room)
    },
    // 修改房间
    update(id, room) {
        return request.put(`/rooms/${id}`, room)
    },
    // 根据ID删除房间（名下有床位时不允许删除）
    deleteById(id) {
        return request.delete(`/rooms/${id}`);
    },
    // 批量删除房间
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/rooms", {data: ids});
    }
}
export default roomApi