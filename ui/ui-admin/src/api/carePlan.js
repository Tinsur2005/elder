import request from "@/utils/request.js";

const carePlanApi = {
    // 分页查询护理计划列表
    list(carePlanQuery) {
        return request.get("/care-plans", {params: carePlanQuery});
    },
    // 根据ID查询护理计划
    selectById(id) {
        return request.get(`/care-plans/${id}`);
    },
    // 新增护理计划
    add(carePlan) {
        return request.post("/care-plans", carePlan)
    },
    // 修改护理计划
    update(id, carePlan) {
        return request.put(`/care-plans/${id}`, carePlan)
    },
    // 根据ID删除护理计划
    deleteById(id) {
        return request.delete(`/care-plans/${id}`);
    },
    // 批量删除护理计划
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/care-plans", {data: ids});
    },
    // 获取指定护理计划包含的全部护理项目（carePlanItem实体列表）
    getCareItemsById(id) {
        return request.get(`/care-plans/getCareItemsById/${id}`);
    },
    // 更新指定护理计划包含的护理项目（先删后插）
    updateCareItems(id, carePlanItems) {
        return request.put(`/care-plans/updateCareItems/${id}`, carePlanItems)
    }
}
export default carePlanApi