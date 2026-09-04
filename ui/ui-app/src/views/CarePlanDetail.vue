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
  import {useRoute, useRouter} from 'vue-router'
  import carePlanApi from '@/api/carePlan.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const userInfoStore = useUserInfoStore()
  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  //是否为家属登录
  const isFamily = computed(() => userInfoStore.userType === 'family')
  //护理计划对象
  const carePlan = ref({})
  //计划包含的护理项目明细
  const carePlanItems = ref([])
  //是否正在加载（显示加载动画）
  const loading = ref(true)

  // ================== 选项 ==================

  //根据起止日期计算护理计划状态，早于开始日期为待执行，处于起止日期之间为执行中，晚于结束日期为已到期，color为状态文字的颜色
  const getPlanStatus = (startDate, endDate) => {
    const now = new Date()
    //取当天0点作为比较基准，避免时分秒影响日期比较
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const start = startDate ? new Date(startDate.replace(/-/g, '/')) : null
    const end = endDate ? new Date(endDate.replace(/-/g, '/')) : null
    if (start && today < start) return {label: '待执行', color: '#FF976A'}
    if (end && today > end) return {label: '已到期', color: '#999999'}
    return {label: '执行中', color: '#1989FA'}
  }

  // 执行周期选项（周期：0天 1周 2月）
  const executeCycleOptions = [
    {value: 0, label: '每天'},
    {value: 1, label: '每周'},
    {value: 2, label: '每月'},
  ]

  // 周期选项（1-7 = 周一至周日）
  const weekDayOptions = [
    {value: 1, label: '周一'},
    {value: 2, label: '周二'},
    {value: 3, label: '周三'},
    {value: 4, label: '周四'},
    {value: 5, label: '周五'},
    {value: 6, label: '周六'},
    {value: 7, label: '周日'},
  ]

  // ================== 下拉数据 ==================

  //加载护理计划信息
  const loadCarePlan = () => {
    // 老人端查自己的护理计划，家属端查当前选中老人的护理计划
    const elderId = isFamily.value ? userInfoStore.currentElderId : userInfoStore.user.id
    return carePlanApi.list({elderId}).then(result => {
      // 从计划列表中找到本次查看的计划
      const found = result.data.records.find(item => item.id == route.query.id)
      if (found) {
        carePlan.value = found
      }
    })
  }

  //加载计划包含的护理项目明细
  const loadCarePlanItems = () => {
    return carePlanApi.getCareItemsById(route.query.id).then(result => {
      carePlanItems.value = result.data || []
    })
  }

  //两个请求都返回后再关闭加载动画
  Promise.all([loadCarePlan(), loadCarePlanItems()]).finally(() => {
    loading.value = false
  })

  // ================== 方法 ==================

  //拼接执行周期描述（如：每天 07:30:00 / 每周二 09:00:00 / 每月15号 15:00:00）
  const formatExecute = (row) => {
    const cycle = executeCycleOptions.find(option => option.value === row.executeCycle)?.label || ''
    let day = ''
    if (row.executeCycle === 1) {
      day = weekDayOptions.find(option => option.value === row.executeDay)?.label || ''
    } else if (row.executeCycle === 2) {
      day = `${row.executeDay}号`
    }
    return `${cycle}${day} ${row.executeTime}`
  }
</script>

<template>
  <div class="plan-detail">
    <van-nav-bar title="护理计划详情" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 加载中 -->
    <div class="page-loading" v-if="loading">
      <van-loading size="24" vertical>加载中...</van-loading>
    </div>

    <template v-else>
    <!-- 护理计划基本信息 -->
    <div class="detail-card">
      <div class="detail-top">
        <van-tag type="primary">护理</van-tag>
        <span class="detail-name">{{ carePlan.name }}</span>
        <span class="detail-status" :style="{color: getPlanStatus(carePlan.startDate, carePlan.endDate).color}">{{ getPlanStatus(carePlan.startDate, carePlan.endDate).label }}</span>
      </div>
      <div class="detail-info">
        <p><van-icon name="manager-o"/><span>老人：{{ carePlan.elderName }}</span></p>
        <p><van-icon name="medal-o"/><span>护理等级：{{ carePlan.careLevelName }}</span></p>
        <p><van-icon name="user-o"/><span>负责护理员：{{ carePlan.userName }}</span></p>
        <p><van-icon name="calendar-o"/><span>{{ carePlan.startDate }} 至 {{ carePlan.endDate }}</span></p>
      </div>
    </div>

    <!-- 护理项目明细 -->
    <div class="detail-card">
      <div class="detail-title">护理项目</div>
      <div class="plan-item" v-for="row in carePlanItems" :key="row.id">
        <div class="plan-item-top">
          <span class="plan-item-name">{{ row.careItemName }}</span>
          <span class="plan-item-time">{{ formatExecute(row) }}</span>
        </div>
        <p class="plan-item-remark" v-if="row.remark">{{ row.remark }}</p>
      </div>
    </div>
    </template>
  </div>
</template>

<style scoped>
  .plan-detail {
    min-height: 100%;
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
    padding: 14px 16px;
    margin-bottom: 12px;
  }

  .detail-top {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }

  .detail-name {
    flex: 1;
    margin-left: 8px;
    font-size: 15px;
    font-weight: bold;
  }

  .detail-status {
    font-size: 13px;
  }

  .detail-info p {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #666;
    line-height: 24px;
  }

  .detail-info :deep(.van-icon) {
    margin-right: 6px;
    font-size: 14px;
    color: #C8C9CC;
  }

  .detail-title {
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 4px;
  }

  /* 护理项目明细项 */
  .plan-item {
    border-top: 1px solid #F0F0F0;
    padding: 12px 0;
  }

  .plan-item-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .plan-item-name {
    font-size: 14px;
    font-weight: bold;
  }

  .plan-item-time {
    font-size: 13px;
    color: #1989FA;
  }

  .plan-item-remark {
    margin-top: 6px;
    font-size: 12px;
    color: #999;
  }
</style>
