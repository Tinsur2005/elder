import request from "@/utils/request.js";

const elderApi = {
    list(elderQuery) {
        return request.get("/elders", {params: elderQuery});
    },
    deleteById(id) {
        return request.delete(`/elders/${id}`);
    },
    add(elder) {
        return request.post("/elders", elder)
    },
    selectById(id) {
        return request.get(`/elders/${id}`);
    },
    update(id, elder) {
        return request.put(`/elders/${id}`, elder)
    },
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/elders", {data: ids});
    }
}

export default  elderApi