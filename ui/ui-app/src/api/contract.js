import request from '@/utils/request.js'

const contractApi = {
    // 查询指定老人的合同列表（附带 elderName 供展示）
    // 手机端列表一次加载，不传分页参数时后端默认取第一页100条（后续如需分页可加 van-list）
    list(contractQuery) {
        return request.get("/contracts", {params: contractQuery})
    },
    // 根据ID查询合同
    selectById(id) {
        return request.get(`/contracts/${id}`)
    }
}
export default contractApi
