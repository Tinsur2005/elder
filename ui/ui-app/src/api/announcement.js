import request from '@/utils/request.js'

const announcementApi = {
    // 分页查询已发布的公告
    list(announcementQuery) {
        return request.get("/announcements", {params: announcementQuery})
    },
    // 根据ID查询公告详情
    selectById(id) {
        return request.get(`/announcements/${id}`)
    }
}
export default announcementApi