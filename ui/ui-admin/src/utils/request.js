import axios from 'axios'

// const baseURL = 'http://localhost:8080'
const baseURL = '/api'
//这个request和axios具有相同的功能
const request = axios.create({baseURL})

//添加响应的拦截器
request.interceptors.response.use(
    response => {
        //返回result
        return response.data
    },
    error => {
        //判断响应状态码,如果为401,则证明未登录,提示请登录,并跳转到登录页面
        if (error.response.status === 401) {
            //ElMessage.error('请先登录')
            //router.push('/login')
        } else {
            //ElMessage.error('服务异常')
        }

        return Promise.reject(error);//异步的状态转化成失败的状态
    }
)

export default request