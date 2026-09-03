<script setup>
  import {ref} from 'vue'
  import {showToast} from 'vant'
  import {useRoute, useRouter} from 'vue-router'
  import newsApi from '@/api/news.js'

  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  //资讯对象
  const news = ref({})

  // ================== 下拉数据 ==================

  //加载资讯详情（后端同时给阅读量+1）
  const loadNews = () => {
    newsApi.selectById(route.query.id).then(result => {
      if (result.code === 1) {
        news.value = result.data
      } else {
        showToast(result.msg)
      }
    })
  }

  loadNews()
</script>

<template>
  <div class="news-detail">
    <van-nav-bar title="资讯详情" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 资讯内容 -->
    <div class="detail-card">
      <h3 class="detail-title">{{ news.title }}</h3>
      <p class="detail-meta">
        <span v-if="news.categoryName">{{ news.categoryName }}</span>
        <span v-if="news.author">{{ news.author }}</span>
        <span>{{ news.createTime ? news.createTime.slice(0, 10) : '-' }}</span>
        <span>{{ news.views == null ? 0 : news.views }}次阅读</span>
      </p>
      <div class="detail-divider"></div>
      <!-- 富文本正文（后台富文本编辑器生成的HTML） -->
      <div class="detail-content" v-html="news.content || '暂无内容'"></div>
    </div>
  </div>
</template>

<style scoped>
  .news-detail {
    min-height: 100vh;
    padding: 12px 12px 20px;
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

  .detail-meta {
    margin-top: 8px;
    font-size: 12px;
    color: #999;
    display: flex;
    align-items: baseline;
    flex-wrap: wrap;
    gap: 12px;
  }

  .detail-divider {
    height: 1px;
    background-color: #F0F0F0;
    margin: 12px 0;
  }

  /* 富文本正文 */
  .detail-content {
    font-size: 14px;
    color: #666;
    line-height: 24px;
    word-break: break-all;
  }

  /* 富文本里的图片不超出卡片 */
  .detail-content :deep(img) {
    max-width: 100%;
    height: auto;
  }
</style>