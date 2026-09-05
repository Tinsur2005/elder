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

const buildingApi = {
    // 分页查询楼栋列表
    list(buildingQuery) {
        return request.get("/buildings", {params: buildingQuery});
    },
    // 根据ID查询楼栋
    selectById(id) {
        return request.get(`/buildings/${id}`);
    },
    // 获取全部楼栋列表List（供楼层、房间、床位等页面的楼栋下拉框使用）
    listAll() {
        return request.get(`/buildings/list`)
    },
    // 新增楼栋
    add(building) {
        return request.post("/buildings", building)
    },
    // 修改楼栋
    update(id, building) {
        return request.put(`/buildings/${id}`, building)
    },
    // 根据ID删除楼栋（名下有楼层时不允许删除）
    deleteById(id) {
        return request.delete(`/buildings/${id}`);
    },
    // 批量删除楼栋
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/buildings", {data: ids});
    }
}
export default buildingApi