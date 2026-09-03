import request from '@/utils/request.js'

const newsApi = {
    // 分页查询已发布的资讯（按发布时间倒序）
    list(newsQuery) {
        return request.get("/news", {params: newsQuery})
    },
    // 根据ID查询资讯详情（阅读量+1）
    selectById(id) {
        return request.get(`/news/${id}`)
    }
}
export default newsApi