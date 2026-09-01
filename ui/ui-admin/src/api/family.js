import request from "@/utils/request.js";

const familyApi = {
    list(familyQuery) {
        return request.get("/family", {params: familyQuery});
    },
    deleteById(id) {
        return request.delete(`/family/${id}`);
    },
    add(family) {
        return request.post("/family", family)
    },
    selectById(id) {
        return request.get(`/family/${id}`);
    },
    update(id, family) {
        return request.put(`/family/${id}`, family)
    },
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/family", {data: ids});
    },
    getEldersById(id) {
        // 根据id获取该家属关联的老人
        return request.get(`/family/getEldersById/${id}`);
    },
    searchByName(name) {
        // 根据真实姓名（可输入部分或全部）模糊搜索家属，供"远程搜索"下拉框使用
        return request.get("/family/searchByName", {params: {name}});
    },
    updateEldersById(id, elderIds) {
        // 根据家属id来修改关联的老人（修改的是elder-family中间表的数据）
        return request.put(`/family/updateElders/${id}`, elderIds)
    }
}

export default  familyApi