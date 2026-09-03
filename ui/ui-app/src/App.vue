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
  // 全局主题变量：清新蓝色系，页面浅灰底和白色圆角卡片
  const themeVars = {
    '--van-primary-color': '#1989FA',
    '--van-blue': '#1989FA',
    '--van-nav-bar-title-text-color': '#323233',
    '--van-nav-bar-height': '46px',
    '--van-cell-group-inset-radius': '8px',
    '--van-tag-primary-color': '#1989FA',
    '--van-tabbar-item-active-color': '#1989FA',
    '--van-tabs-bottom-bar-color': '#1989FA',
  }
</script>

<template>
  <!-- 顶层路由 /login   /(路由到Index) -->
  <van-config-provider :theme-vars="themeVars">
    <router-view></router-view>
  </van-config-provider>
</template>

<style scoped>
</style>

<style>
  * {
    box-sizing: border-box;
    margin: 0;
  }

  body {
    font-family:
      -apple-system,
      BlinkMacSystemFont,
      'Helvetica Neue',
      Helvetica,
      Segoe UI,
      Arial,
      Roboto,
      'PingFang SC',
      'miui',
      'Hiragino Sans GB',
      'Microsoft Yahei',
      sans-serif;
    font-size: 14px;
    color: #323233;
    background-color: #F5F6FA;
    -webkit-font-smoothing: antialiased;
  }
</style>
