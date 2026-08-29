import request from "@/utils/request.js";

const contractApi = {
    // 分页查询合同列表
    list(contractQuery) {
        return request.get("/contracts", {params: contractQuery});
    },
    // 根据ID删除合同
    deleteById(id) {
        return request.delete(`/contracts/${id}`);
    },
    // 新增合同
    add(contract) {
        return request.post("/contracts", contract)
    },
    // 根据ID查询合同
    selectById(id) {
        return request.get(`/contracts/${id}`);
    },
    // 修改合同
    update(id, contract) {
        return request.put(`/contracts/${id}`, contract)
    },
    // 批量删除合同
    deleteAll(ids) {
        // axios 的 delete 第2个参数是 config，请求体必须放在 data 字段里
        return request.delete("/contracts", {data: ids});
    }
}

export default  contractApi