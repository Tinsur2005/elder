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

const examPackageApi = {
    //分页查询体检套餐列表
    list(examPackageQuery) {
        return request.get("/exam-packages", {params: examPackageQuery});
    },
    //根据ID查询体检套餐
    selectById(id) {
        return request.get(`/exam-packages/${id}`);
    },
    //获取全部上架状态的体检套餐列表（供体检预约选择套餐时使用）
    listAll() {
        return request.get(`/exam-packages/list`)
    },
    //新增体检套餐
    add(examPackage) {
        return request.post("/exam-packages", examPackage)
    },
    //修改体检套餐
    update(id, examPackage) {
        return request.put(`/exam-packages/${id}`, examPackage)
    },
    //根据ID删除体检套餐
    deleteById(id) {
        return request.delete(`/exam-packages/${id}`);
    },
    //批量删除体检套餐
    deleteAll(ids) {
        return request.delete("/exam-packages", {data: ids});
    },
    //获取指定体检套餐包含的全部体检项目（examPackageItem实体列表）
    getPackageItemsById(id) {
        return request.get(`/exam-packages/getPackageItemsById/${id}`);
    },
    //更新指定体检套餐包含的体检项目
    updatePackageItems(id, examPackageItems) {
        return request.put(`/exam-packages/updatePackageItems/${id}`, examPackageItems)
    }
}
export default examPackageApi