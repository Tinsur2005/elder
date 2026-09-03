import request from '@/utils/request.js'

const userApi = {
    // 登录（userType：elder老人 / family家属，与登录页Tab对应）
    login(user) {
        return request.post("/users/login", user)
    },
    // 根据Token查询登录用户信息（老人或家属），家属同时返回绑定的老人列表
    userInfo() {
        return request.get("/users/userInfo")
    }
}
export default userApi