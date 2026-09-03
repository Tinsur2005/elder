import request from '@/utils/request.js'

const careTaskApi = {
    // 查询指定老人的护理任务列表（可按状态过滤，附带 elderName、userName 供展示）
    // 手机端列表一次加载，不传分页参数时后端默认取第一页100条（后续如需分页可加 van-list）
    list(careTaskQuery) {
        return request.get("/care-tasks", {params: careTaskQuery})
    },
    // 根据ID查询护理任务详情
    selectById(id) {
        return request.get(`/care-tasks/${id}`)
    }
}
export default careTaskApi