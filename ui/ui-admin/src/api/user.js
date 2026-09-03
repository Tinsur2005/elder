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

const userApi = {
    login(user) {
        return request.post("/users/login", user)
    },
    list(userQuery) {
        return request.get("/users", {params: userQuery});
    },
    deleteById(id) {
        //return service.delete("/users/" + id);
        return request.delete(`/users/${id}`);
    },
    add(user) {
        return request.post("/users", user)
    },
    selectById(id) {
        return request.get(`/users/${id}`);
    },
    update(id, user) {
        return request.put(`/users/${id}`, user)
    },
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/users", {data: ids});
    },
    userInfo() {
        return request.get(`/users/userInfo`)
    },
    resetPassword(userPasswordDTO) {
        return request.put(`/users/resetPassword`, userPasswordDTO)
    },
    exportExcel() {
        return request({
            url: `/users/exportExcel`,
            method: 'get',
            //XMLHttpRequest 属性 responseType 是一个枚举字符串值，用于指定响应中包含的数据类型。
            //"blob": response 是一个包含二进制数据的 Blob 对象。
            responseType: 'blob'
        })
    },getRolesById(id) {
        // 根据id获取用户的角色ID
        return request.get(`/users/getRolesById/${id}`);
    },updateRolesById(id, roles) {
        // 根据用户id来修改角色（修改的是user_roles中间表的数据）
        return request.put(`/users/updateRoles/${id}`, roles)
    },searchByRole(roleId, name) {
        // 根据角色id（可加姓名关键字）搜索用户，供护理计划等"选护理人员"远程下拉框使用
        return request.get("/users/searchByRole", {params: {roleId, name}});
    },searchByName(name) {
        // 按真实姓名（可输入部分或全部）模糊搜索所有用户，供护理计划等"选护理人员"远程下拉框使用，不限定角色
        return request.get("/users/searchByName", {params: {name}});
    },
}

export default  userApi