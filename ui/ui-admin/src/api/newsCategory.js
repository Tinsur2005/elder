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

const newsCategoryApi = {
    // 分页查询资讯分类列表
    list(newsCategoryQuery) {
        return request.get("/news-categories", {params: newsCategoryQuery});
    },
    // 获取全部启用状态的资讯分类列表（供资讯编辑时"选分类"下拉框使用）
    listAll() {
        return request.get("/news-categories/list");
    },
    // 根据ID查询资讯分类
    selectById(id) {
        return request.get(`/news-categories/${id}`);
    },
    // 新增资讯分类
    add(newsCategory) {
        return request.post("/news-categories", newsCategory)
    },
    // 修改资讯分类
    update(id, newsCategory) {
        return request.put(`/news-categories/${id}`, newsCategory)
    },
    // 根据ID删除资讯分类
    deleteById(id) {
        return request.delete(`/news-categories/${id}`);
    },
    // 批量删除资讯分类
    deleteAll(ids) {
        return request.delete("/news-categories", {data: ids});
    }
}
export default newsCategoryApi