/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
import axios from 'axios'
import {showToast} from 'vant'
import router from '@/router'

const baseURL = '/app/api'
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
        //返回result
        return response.data
    },
    error => {
        //判断响应状态码,如果为401,则证明未登录,提示请登录
        if (error.response && error.response.status === 401) {
            //清除过期 token，否则路由守卫又会将用户弹回登录页造成死循环
            const tokenStore = useTokenStore()
            tokenStore.removeToken()
            //防止首页多个请求同时 401 导致提示刷屏、重复跳转
            if (!isRedirectingToLogin) {
                isRedirectingToLogin = true
                showToast('登录失效，请先登录')
                router.push('/login')
                setTimeout(() => { isRedirectingToLogin = false }, 2000)
            }
        } else {
            showToast('服务异常')
        }
        return Promise.reject(error);//异步的状态转化成失败的状态
    }
)

export default request
