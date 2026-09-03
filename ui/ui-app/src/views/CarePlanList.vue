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
  import {computed, ref} from 'vue'
  import {useRouter} from 'vue-router'
  import carePlanApi from '@/api/carePlan.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const userInfoStore = useUserInfoStore()
  const router = useRouter()

  // ================== 对象 ==================

  // 当前查看的老人：老人登录是自己，家属登录是当前选中的绑定老人
  const currentElder = computed(() => {
    if (userInfoStore.userType === 'family') {
      return userInfoStore.elders.find(item => item.id === userInfoStore.currentElderId) || {}
    }
    return userInfoStore.user
  })

  //是否为家属登录（家属页面标题显示当前老人姓名）
  const isFamily = computed(() => userInfoStore.userType === 'family')

  //护理计划列表
  const list = ref([])
  //是否正在加载（显示加载动画）
  const loading = ref(true)

  // ================== 选项 ==================

  // 状态选项（状态：0结束 1开始），color为卡片右侧状态文字的颜色
  const statusOptions = [
    {value: 1, label: '进行中', color: '#1989FA'},
    {value: 0, label: '已结束', color: '#999999'},
  ]

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    // 老人端查自己的护理计划，家属端查当前选中老人的护理计划
    const elderId = isFamily.value ? userInfoStore.currentElderId : userInfoStore.user.id
    carePlanApi.list({elderId}).then(result => {
      list.value = result.data.records
      loading.value = false
    })
  }

  loadData()

  //根据状态获取展示信息
  const getStatus = (value) => {
    return statusOptions.find(option => option.value === value) || statusOptions[1]
  }

  //跳转到护理计划详情
  const goDetail = (row) => {
    router.push({path: '/carePlanDetail', query: {id: row.id}})
  }
</script>

<template>
  <div class="plan-list">
    <van-nav-bar :title="isFamily ? currentElder.realName + '的护理计划' : '护理计划'" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 加载中 -->
    <div class="page-loading" v-if="loading">
      <van-loading size="24" vertical>加载中...</van-loading>
    </div>

    <template v-else>
      <!-- 空状态 -->
      <van-empty description="暂无护理计划" v-if="list.length === 0"/>

      <!-- 护理计划列表 -->
      <div
          class="plan-card"
          v-for="row in list"
          :key="row.id"
          @click="goDetail(row)"
      >
      <!-- 类别标签 + 计划名称 + 右侧状态文字 -->
      <div class="plan-top">
        <van-tag type="primary">护理</van-tag>
        <span class="plan-name">{{ row.name }}</span>
        <span class="plan-status" :style="{color: getStatus(row.status).color}">{{ getStatus(row.status).label }}</span>
      </div>
      <!-- 摘要信息行（灰色小图标 + 文本） -->
      <div class="plan-info">
        <p><van-icon name="medal-o"/><span>护理等级：{{ row.careLevelName }}</span></p>
        <p><van-icon name="manager-o"/><span>负责护理员：{{ row.userName }}</span></p>
        <p><van-icon name="calendar-o"/><span>{{ row.startDate }} 至 {{ row.endDate }}</span></p>
      </div>
      <!-- 底部：查看详情 -->
      <div class="plan-footer">
        <span>查看计划详情</span>
        <van-icon name="arrow" color="#1989FA"/>
      </div>
    </div>
    </template>
  </div>
</template>

<style scoped>
  .plan-list {
    min-height: 100vh;
    padding: 12px 12px 20px;
  }

  /* 加载中 */
  .page-loading {
    display: flex;
    justify-content: center;
    padding: 60px 0;
  }

  /* 护理计划卡片：类别标签 + 名称 + 状态文字 + 图标信息行 */
  .plan-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 14px 16px;
    margin-bottom: 10px;
  }

  .plan-top {
    display: flex;
    align-items: center;
  }

  .plan-name {
    flex: 1;
    margin-left: 8px;
    font-size: 15px;
    font-weight: bold;
  }

  .plan-status {
    font-size: 13px;
  }

  .plan-info {
    margin-top: 10px;
  }

  .plan-info p {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #666;
    line-height: 24px;
  }

  .plan-info :deep(.van-icon) {
    margin-right: 6px;
    font-size: 14px;
    color: #C8C9CC;
  }

  .plan-footer {
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #F0F0F0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 13px;
    color: #999;
  }
</style>