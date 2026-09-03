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
  import careTaskApi from '@/api/careTask.js'
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

  //护理任务列表
  const list = ref([])
  //当前选中的状态Tab（与任务状态一一对应）
  const activeStatus = ref(0)
  //查看范围：today仅看今天 / all查看全部
  const viewScope = ref('today')

  // ================== 选项 ==================

  // 任务状态Tab（状态：0待执行 1已完成 2已跳过/取消），color 为卡片右侧状态文字的颜色
  const statusTabs = [
    {value: 0, label: '待执行', color: '#FF976A'},
    {value: 1, label: '已完成', color: '#07C160'},
    {value: 2, label: '已跳过', color: '#999999'},
  ]

  // 查看范围选项
  const viewOptions = [
    {text: '仅看今天', value: 'today'},
    {text: '查看全部', value: 'all'}
  ]

  // ================== 方法 ==================

  //拼出今天的yyyy-MM-dd
  const getToday = () => {
    const d = new Date()
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  }

  //加载数据（按状态Tab + 查看范围过滤）
  const loadData = () => {
    // 老人端查自己的护理任务，家属端查当前选中老人的护理任务
    const elderId = isFamily.value ? userInfoStore.currentElderId : userInfoStore.user.id
    // 仅看今天时传计划执行日期范围，查看全部不传
    const query = {elderId, status: activeStatus.value}
    if (viewScope.value === 'today') {
      const today = getToday()
      query.beginPlanExecuteDate = `${today} 00:00:00`
      query.endPlanExecuteDate = `${today} 23:59:59`
    }
    careTaskApi.list(query).then(result => {
      list.value = result.data.records
    })
  }

  loadData()

  //切换状态Tab或查看范围
  const onTabChange = () => {
    loadData()
  }

  //根据状态获取展示信息
  const getStatus = (value) => {
    return statusTabs.find(option => option.value === value) || statusTabs[0]
  }

  //跳转到护理任务详情
  const goDetail = (row) => {
    router.push({path: '/careTaskDetail', query: {id: row.id}})
  }
</script>

<template>
  <div class="task-list">
    <van-nav-bar :title="isFamily ? currentElder.realName + '的护理任务' : '护理任务'" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 状态Tab + 查看范围下拉 -->
    <div class="task-tabs">
      <van-tabs v-model:active="activeStatus" @change="onTabChange" shrink>
        <van-tab v-for="tab in statusTabs" :key="tab.value" :title="tab.label" :name="tab.value"/>
      </van-tabs>
      <van-dropdown-menu class="task-scope">
        <van-dropdown-item v-model="viewScope" :options="viewOptions" @change="onTabChange"/>
      </van-dropdown-menu>
    </div>

    <!-- 空状态 -->
    <van-empty description="暂无护理任务" v-if="list.length === 0"/>

    <!-- 护理任务列表 -->
    <div class="task-cards">
      <div
          class="task-card"
          v-for="row in list"
          :key="row.id"
          @click="goDetail(row)"
      >
        <!-- 类别标签 + 项目名称 + 右侧状态文字 -->
        <div class="task-top">
          <van-tag type="primary">护理</van-tag>
          <span class="task-name">{{ row.careItemName }}</span>
          <span class="task-status" :style="{color: getStatus(row.status).color}">{{ getStatus(row.status).label }}</span>
        </div>
        <!-- 摘要信息行（灰色小图标 + 文本） -->
        <div class="task-info">
          <p><van-icon name="calendar-o"/><span>{{ row.planExecuteDate }} {{ row.planExecuteTime }}</span></p>
          <p><van-icon name="manager-o"/><span>{{ row.userName }}</span></p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .task-list {
    min-height: 100vh;
    padding-bottom: 20px;
  }

  /* 状态Tab + 查看范围下拉 */
  .task-tabs {
    background-color: #FFFFFF;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .task-scope {
    flex-shrink: 0;
  }

  /* 去掉下拉菜单自带的投影和内边距，与状态Tab融为同一行 */
  .task-scope :deep(.van-dropdown-menu__bar) {
    background-color: transparent;
    box-shadow: none;
  }

  .task-scope :deep(.van-dropdown-menu__title) {
    font-size: 13px;
    padding: 0 12px 0 0;
  }

  .task-cards {
    padding: 12px 12px 0;
  }

  /* 护理任务卡片：类别标签 + 名称 + 状态文字 + 图标信息行 */
  .task-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 14px 16px;
    margin-bottom: 10px;
  }

  .task-top {
    display: flex;
    align-items: center;
  }

  .task-name {
    flex: 1;
    margin-left: 8px;
    font-size: 15px;
    font-weight: bold;
  }

  .task-status {
    font-size: 13px;
  }

  .task-info {
    margin-top: 10px;
  }

  .task-info p {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #666;
    line-height: 24px;
  }

  .task-info :deep(.van-icon) {
    margin-right: 6px;
    font-size: 14px;
    color: #C8C9CC;
  }
</style>
