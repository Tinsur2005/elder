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

const announcementApi = {
    // 分页查询公告列表
    list(announcementQuery) {
        return request.get("/announcements", {params: announcementQuery});
    },
    // 根据ID查询公告
    selectById(id) {
        return request.get(`/announcements/${id}`);
    },
    // 新增公告
    add(announcement) {
        return request.post("/announcements", announcement)
    },
    // 修改公告
    update(id, announcement) {
        return request.put(`/announcements/${id}`, announcement)
    },
    // 根据ID删除公告
    deleteById(id) {
        return request.delete(`/announcements/${id}`);
    },
    // 批量删除公告
    deleteAll(ids) {
        return request.delete("/announcements", {data: ids});
    }
}
export default announcementApi