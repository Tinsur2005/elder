// 创建一个路由器，并暴露出去
// 第一步：引入createRouter
import {createRouter, createWebHistory} from 'vue-router'
// 引入一个一个可能要呈现组件
import Index from '@/views/Index.vue'
import Login from '@/views/Login.vue'
import User from '@/views/User.vue'
import Elder from '@/views/Elder.vue'

//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: [
        {path: '/login', component: Login},
        {
            path: '/', component: Index, children: [
                {path: '/user', component: User},
                {path: '/elder', component: Elder}
            ]
        }
    ]
})

// 暴露出去router
export default router