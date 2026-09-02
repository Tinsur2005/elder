import axios from 'axios'
import {ElMessage} from 'element-plus'
import router from '@/router'

const baseURL = '/admin/api'
const request = axios.create({baseURL})

import {useTokenStore} from "@/store/token.js";
// 标记是否正在跳转登录页，防止同一批 401 响应弹出多条提示、多次跳转
let isRedirectingToLogin = false

//添加请求拦截器
request.interceptors.request.use(
    config => {
        //请求之前回调
        //添加Token
        const tokenStore = useTokenStore()
        //判断token是否为空
        if(tokenStore && tokenStore.token) {
            config.headers.Authorization = tokenStore.token
        }
        return config
    },
    error => {
        return Promise.reject(error) //请求失败
    }
)

//添加响应的拦截器
request.interceptors.response.use(
    response => {
        //blob 响应(如 excel 导出)需要读取响应头里的文件名,返回完整 response ，而不是解析成json
        if (response.config.responseType === 'blob') {
            return response;
        }
        //返回result
        return response.data
    },
    error => {
        //判断响应状态码,如果为401,则证明未登录,提示请登录
        if (error.response && error.response.status === 401) {
            //清除过期 token，否则路由守卫又会将用户弹回首页造成死循环
            const tokenStore = useTokenStore()
            tokenStore.removeToken()
            //防止首页多个请求同时 401 导致提示刷屏、重复跳转
            if (!isRedirectingToLogin) {
                isRedirectingToLogin = true
                ElMessage.error('登录失效，请先登录')
                router.push('/login')
                setTimeout(() => { isRedirectingToLogin = false }, 2000)
            }
        } else {
            ElMessage.error('服务异常')
        }
        return Promise.reject(error);//异步的状态转化成失败的状态
    }
)

export default request