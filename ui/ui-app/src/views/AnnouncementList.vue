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
  import {useRouter} from 'vue-router'
  import announcementApi from '@/api/announcement.js'

  const router = useRouter()

  // ================== 对象 ==================

  //公告列表（van-list 滚动分页加载）
  const list = ref([])
  //当前页码
  const page = ref(1)
  //每页条数
  const limit = 10
  //是否正在加载（van-list）
  const loading = ref(false)
  //是否已全部加载完（van-list）
  const finished = ref(false)

  // ================== 方法 ==================

  //滚动到底部分页加载已发布的公告
  const onLoad = () => {
    announcementApi.list({page: page.value, limit}).then(result => {
      const records = result.data.records || []
      list.value.push(...records)
      // 不足一页或已达总数时说明加载完毕
      if (records.length < limit || list.value.length >= result.data.total) {
        finished.value = true
      } else {
        page.value = page.value + 1
      }
      loading.value = false
    })
  }

  //跳转公告详情
  const goDetail = (row) => {
    router.push({path: '/announcementDetail', query: {id: row.id}})
  }
</script>

<template>
  <div class="announcement-list">
    <van-nav-bar title="公告列表" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 空状态 -->
    <van-empty description="暂无公告" v-if="finished && list.length === 0"/>

    <!-- 公告列表（滚动分页加载） -->
    <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
    >
      <div
          class="announcement-card"
          v-for="row in list"
          :key="row.id"
          @click="goDetail(row)"
      >
        <p class="announcement-title">{{ row.title }}</p>
        <p class="announcement-date">{{ row.createTime ? row.createTime.slice(0, 10) : '' }}</p>
      </div>
    </van-list>
  </div>
</template>

<style scoped>
  .announcement-list {
    min-height: 100vh;
    padding: 12px 12px 20px;
  }

  /* 公告卡片：标题 + 日期 */
  .announcement-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 14px 16px;
    margin-bottom: 10px;
  }

  .announcement-title {
    font-size: 15px;
    font-weight: bold;
    color: #323233;
    line-height: 22px;
  }

  .announcement-date {
    margin-top: 6px;
    font-size: 12px;
    color: #999;
  }
</style>