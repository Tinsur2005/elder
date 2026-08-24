import { createApp } from 'vue'
import App from './App.vue'

import router from './router'
import {createPinia} from 'pinia'
const pinia = createPinia()
import ElementPlus from 'element-plus' //导入element-plus
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css' //导入element-plus样式

createApp(App).use(router).use(pinia).use(ElementPlus, {locale: zhCn }).mount('#app')
