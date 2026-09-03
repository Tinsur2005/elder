/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝
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

const newsApi = {
    // 分页查询资讯列表
    list(newsQuery) {
        return request.get("/news", {params: newsQuery});
    },
    // 根据ID查询资讯
    selectById(id) {
        return request.get(`/news/${id}`);
    },
    // 新增资讯
    add(news) {
        return request.post("/news", news)
    },
    // 修改资讯
    update(id, news) {
        return request.put(`/news/${id}`, news)
    },
    // 根据ID删除资讯
    deleteById(id) {
        return request.delete(`/news/${id}`);
    },
    // 批量删除资讯
    deleteAll(ids) {
        return request.delete("/news", {data: ids});
    }
}
export default newsApi