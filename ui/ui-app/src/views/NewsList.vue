<script setup>
  import {ref} from 'vue'
  import {useRouter} from 'vue-router'
  import newsApi from '@/api/news.js'

  const router = useRouter()

  // ================== 对象 ==================

  //资讯列表（van-list 滚动分页加载）
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

  //滚动到底部分页加载已发布的资讯
  const onLoad = () => {
    newsApi.list({page: page.value, limit}).then(result => {
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

  //资讯展示日期（createTime 截取 MM-DD）
  const getNewsDate = (createTime) => {
    return createTime ? createTime.slice(5, 10) : ''
  }

  //跳转资讯详情
  const goDetail = (row) => {
    router.push({path: '/newsDetail', query: {id: row.id}})
  }
</script>

<template>
  <div class="news-list">
    <van-nav-bar title="资讯列表" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 空状态 -->
    <van-empty description="暂无资讯" v-if="finished && list.length === 0"/>

    <!-- 资讯列表（滚动分页加载） -->
    <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
    >
      <div
          class="news-card"
          v-for="item in list"
          :key="item.id"
          @click="goDetail(item)"
      >
        <van-image class="news-cover" width="86" height="64" radius="8" fit="cover" :src="item.coverImage">
          <template #loading>
            <div class="news-cover-placeholder"><van-icon name="photo-o" size="22" color="#CCCCCC"/></div>
          </template>
          <template #error>
            <div class="news-cover-placeholder"><van-icon name="photo-o" size="22" color="#CCCCCC"/></div>
          </template>
        </van-image>
        <div class="news-info">
          <p class="news-title">{{ item.title }}</p>
          <p class="news-summary" v-if="item.summary">{{ item.summary }}</p>
          <p class="news-meta">{{ item.categoryName }} · {{ getNewsDate(item.createTime) }}</p>
        </div>
      </div>
    </van-list>
  </div>
</template>

<style scoped>
  .news-list {
    min-height: 100%;
    padding: 12px 12px 20px;
  }

  /* 资讯图文卡片 */
  .news-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 10px;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .news-cover {
    flex-shrink: 0;
  }

  .news-cover-placeholder {
    width: 86px;
    height: 64px;
    background-color: #F5F6FA;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .news-info {
    flex: 1;
    min-width: 0;
  }

  .news-title {
    font-size: 15px;
    font-weight: bold;
    color: #323233;
    line-height: 21px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

  .news-summary {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
    line-height: 17px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
  }

  .news-meta {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
  }
</style>