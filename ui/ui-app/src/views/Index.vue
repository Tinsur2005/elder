<!--
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
-->
<script setup>
  import {computed} from 'vue'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const userInfoStore = useUserInfoStore()

  // 底部导航栏（老人端4个Tab：首页/体检/AI对话/我的；家属端2个Tab：首页/我的）
  const tabbarItems = computed(() => {
    if (userInfoStore.userType === 'family') {
      return [
        {path: '/home', title: '首页', icon: 'wap-home-o'},
        {path: '/profile', title: '我的', icon: 'user-o'}
      ]
    }
    return [
      {path: '/home', title: '首页', icon: 'wap-home-o'},
      {path: '/exam', title: '体检', icon: 'notes-o'},
      {path: '/aiChat', title: 'AI对话', icon: 'chat-o'},
      {path: '/profile', title: '我的', icon: 'user-o'}
    ]
  })
</script>

<template>
  <!-- 顶层路由内容区 + 底部导航栏 -->
  <router-view></router-view>
  <van-tabbar route safe-area-inset-bottom placeholder>
    <van-tabbar-item
        v-for="item in tabbarItems"
        :key="item.path"
        :to="item.path"
        :icon="item.icon"
    >{{ item.title }}</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
</style>
