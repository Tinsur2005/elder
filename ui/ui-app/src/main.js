import { createApp } from 'vue'
import App from './App.vue'

import router from './router'
import {createPinia} from 'pinia'
//引入持久化插件
import piniaPluginPersistedstate from "pinia-plugin-persistedstate"
//引入 vant
import Vant from 'vant'
//引入 vant 样式
import 'vant/lib/index.css'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

const app = createApp(App)

app.use(router).use(pinia).use(Vant).mount('#app')
