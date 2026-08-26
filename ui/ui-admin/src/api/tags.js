import request from "@/utils/request.js";

const tagsApi = {
    list(id) {
        return request.get(`/tags/list`)
    }
}
export default  tagsApi