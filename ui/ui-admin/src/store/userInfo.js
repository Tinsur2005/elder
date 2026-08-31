import {defineStore} from "pinia";

export const useUserInfoStore = defineStore('userInfo', {
    //存储数据点的地方
    state() {
        return {
            user: {},
            btnList: []
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
        setBtnList(btnList) {
            this.btnList = btnList
        },
        removeBtnList() {
            this.btnList = []
        }
    },
    persist: {
        enabled: true,  //开启缓存，存储在本地localstorage
    }
})