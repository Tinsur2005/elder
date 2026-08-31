import request from "@/utils/request.js";

const rolesApi = {
    // 分页查询角色列表
    list(roleQuery) {
        return request.get("/roles", {params: roleQuery});
    },
    // 获取全部角色列表List（供用户勾选角色时用）
    listAll() {
        return request.get(`/roles/list`)
    },
    // 根据ID查询角色
    selectById(id) {
        return request.get(`/roles/${id}`);
    },
    // 新增角色
    add(role) {
        return request.post("/roles", role)
    },
    // 修改角色
    update(id, role) {
        return request.put(`/roles/${id}`, role)
    },
    // 根据ID删除角色
    deleteById(id) {
        return request.delete(`/roles/${id}`);
    },
    // 批量删除角色
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/roles", {data: ids});
    },
    // 查询该角色已分配的权限id集合（用于权限回显）
    selectPermissionById(id) {
        return request.get(`/roles/selectPermissionById/${id}`);
    },
    // 给角色分配权限（先删后插，传入权限id数组）
    updatePermission(id, permissionIds) {
        return request.put(`/roles/updatePermission/${id}`, permissionIds);
    }
}
export default rolesApi