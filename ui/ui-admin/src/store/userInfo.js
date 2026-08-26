import {defineStore} from "pinia";

export const useUserInfoStore = defineStore('userInfo', {
    //存储数据点的地方
    state() {
        return {
            user: {}
        }
    },
    //方法
    actions: {
        setUserInfo(user) {
            this.user = user
        },
        removeUserInfo() {
            this.user={}
        }
    },
    persist:{
        enabled: true,  //开启缓存，存储在本地localstorage
    }
    }
)