import request from '@/utils/request.js'


const permissionApi = {
    // 查询权限树形结构
    selectPermissionTree() {
        return request.get('/permissions/selectPermissionTree')
    },
    // 根据ID查询权限
    selectById(id) {
        return request.get(`/permissions/${id}`)
    },
    // 新增权限
    add(permission) {
        return request.post('/permissions', permission)
    },
    // 修改权限
    update(permission) {
        return request.put(`/permissions/${permission.id}`, permission)
    },
    // 根据ID删除权限
    deleteById(id) {
        return request.delete(`/permissions/${id}`)
    },
}

export default permissionApi
