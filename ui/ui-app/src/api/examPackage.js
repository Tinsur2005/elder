import request from '@/utils/request.js'

const examPackageApi = {
    // 获取全部上架状态的体检套餐（供预约页面选择）
    listAll() {
        return request.get("/exam-packages/list")
    },
    // 根据套餐id获取套餐包含的体检项目（项目名称需前端用体检项目列表按 examItemId 映射）
    getPackageItemsById(id) {
        return request.get(`/exam-packages/getPackageItemsById/${id}`)
    }
}
export default examPackageApi