import request from '@/utils/request.js'

const examItemApi = {
    // 获取全部启用状态的体检项目（供预约页面把套餐包含的项目id映射成名称）
    listAll() {
        return request.get("/exam-items/list")
    }
}
export default examItemApi