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
    }
}

export default  userApi