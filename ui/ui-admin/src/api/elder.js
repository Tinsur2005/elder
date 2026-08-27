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
    },getTagsById(id) {
        // 根据id获取老人的标注标签
        return request.get(`/elders/getTagsById/${id}`);
    },
    updateTagsById(id, tags) {
        // 根据老人id来修改标注标签（修改的是elder-tags中间表的数据）
        return request.put(`/elders/updateTags/${id}`, tags)
    },
    exportExcel() {
        return request({
            url: `/elders/exportExcel`,
            method: 'get',
            //XMLHttpRequest 属性 responseType 是一个枚举字符串值，用于指定响应中包含的数据类型。
            //"blob": response 是一个包含二进制数据的 Blob 对象。
            responseType: 'blob'
        })
    }
}

export default  elderApi