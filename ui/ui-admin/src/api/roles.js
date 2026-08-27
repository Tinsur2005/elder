import request from "@/utils/request.js";

const rolesApi = {
    // 获取全部标签列表List（供用户勾选角色时用）
    listAll() {
        return request.get(`/roles/list`)
    },
    // 根据ID查询用户的角色ID
    selectById(id) {
        return request.get(`/roles/${id}`);
    }
}
export default rolesApi