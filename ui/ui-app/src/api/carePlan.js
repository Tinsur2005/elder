import request from '@/utils/request.js'

const carePlanApi = {
    // 查询指定老人的护理计划列表（附带 elderName、userName、careLevelName 供展示）
    // 手机端列表一次加载，不传分页参数时后端默认取第一页100条（后续如需分页可加 van-list）
    list(carePlanQuery) {
        return request.get("/care-plans", {params: carePlanQuery})
    },
    // 根据计划id获取计划包含的护理项目明细（附带项目名称 careItemName，供详情页展示）
    getCareItemsById(id) {
        return request.get(`/care-plans/getCareItemsById/${id}`)
    }
}
export default carePlanApi