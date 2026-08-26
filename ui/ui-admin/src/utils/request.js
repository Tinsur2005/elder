import axios from 'axios'
import {ElMessage} from 'element-plus'
import router from '@/router'

const baseURL = '/api'
const request = axios.create({baseURL})

import {useTokenStore} from "@/store/token.js";
//添加请求拦截器
request.interceptors.request.use(
    config => {
        //请求之前回调
        //添加Token
        const tokenStore = useTokenStore()
        //判断token是否为空
        if(tokenStore) {
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
        //返回result
        return response.data
    },
    error => {
        //判断响应状态码,如果为401,则证明未登录,提示请登录,并跳转到登录页面
        if (error.response.status === 401) {
            ElMessage.error('登录失效，请先登录')
            router.push('/login')
        } else {
            ElMessage.error('服务异常')
        }
        return Promise.reject(error);//异步的状态转化成失败的状态
    }
)

export default request