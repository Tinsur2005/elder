import {defineStore} from "pinia";

export const useUserInfoStore = defineStore('userInfo', {
    //存储数据点的地方
    state() {
        return {
            user: {},
            // 登录用户的角色（elder老人 / family家属），登录时写入，用于渲染不同的TabBar和首页
            userType: '',
            // 家属绑定的老人列表（家属登录后由接口返回，老人登录时为空数组）
            elders: [],
            // 家属当前正在查看的绑定老人id，切换老人卡片时更新
            currentElderId: null
        }
    },
    //方法
    actions: {
        setUserInfo(user) {
            this.user = user
        },
        removeUserInfo() {
            this.user = {}
        },
        setUserType(userType) {
            this.userType = userType
        },
        removeUserType() {
            this.userType = ''
        },
        setElders(elders) {
            this.elders = elders
        },
        removeElders() {
            this.elders = []
        },
        setCurrentElderId(currentElderId) {
            this.currentElderId = currentElderId
        },
        removeCurrentElderId() {
            this.currentElderId = null
        }
    },
    persist: {
        enabled: true,  //开启缓存，存储在本地localstorage
    }
})
