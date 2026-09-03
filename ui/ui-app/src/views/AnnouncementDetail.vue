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
  import {ref} from 'vue'
  import {useRoute, useRouter} from 'vue-router'
  import announcementApi from '@/api/announcement.js'

  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  //公告对象
  const announcement = ref({})
  //显示加载动画
  const loading = ref(true)

  // ================== 下拉数据 ==================

  //加载公告详情
  const loadAnnouncement = () => {
    announcementApi.selectById(route.query.id).then(result => {
      if (result.code === 1) {
        announcement.value = result.data
      }
      loading.value = false
    })
  }

  loadAnnouncement()
</script>

<template>
  <div class="announcement-detail">
    <van-nav-bar title="公告详情" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 加载中 -->
    <div class="page-loading" v-if="loading">
      <van-loading size="24" vertical>加载中...</van-loading>
    </div>

    <!-- 公告内容 -->
    <div class="detail-card" v-else>
      <h3 class="detail-title">{{ announcement.title }}</h3>
      <p class="detail-date">发布时间：{{ announcement.createTime ? announcement.createTime.slice(0, 10) : '-' }}</p>
      <div class="detail-divider"></div>
      <p class="detail-content">{{ announcement.content || '暂无内容' }}</p>
    </div>
  </div>
</template>

<style scoped>
  .announcement-detail {
    min-height: 100vh;
    padding: 12px 12px 20px;
  }

  /* 加载中 */
  .page-loading {
    display: flex;
    justify-content: center;
    padding: 60px 0;
  }

  .detail-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 16px;
  }

  .detail-title {
    font-size: 17px;
    font-weight: bold;
    color: #323233;
    line-height: 24px;
  }

  .detail-date {
    margin-top: 8px;
    font-size: 12px;
    color: #999;
  }

  .detail-divider {
    height: 1px;
    background-color: #F0F0F0;
    margin: 12px 0;
  }

  .detail-content {
    font-size: 14px;
    color: #666;
    line-height: 24px;
    white-space: pre-wrap;
  }
</style>