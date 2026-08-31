import request from "@/utils/request.js";

const dashboardApi = {
    // 获取首页看板数据（统计卡片数字 + 各图表数据，一次请求全部带回）
    getDashboard() {
        return request.get("/dashboard");
    }
}
export default dashboardApi