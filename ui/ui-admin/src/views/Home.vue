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
  import dashboardApi from '@/api/dashboard.js'
  import EChart from '@/components/EChart.vue'
  import {ref} from 'vue'
  import {useUserInfoStore} from '@/store/userInfo.js'
  import {Timer, User, Document, CollectionTag, UserFilled} from '@element-plus/icons-vue'

  const userInfoStore = useUserInfoStore()

  // ============ 看板数据（进入页面时从后端获取，统计卡片和图表共用一次请求） ============
  const dashboard = ref({})

  //各图表的echarts配置项，异步拿到数据后由构建方法生成
  const weekTaskOption = ref({})
  const todayTaskStatusOption = ref({})
  const contractTypeOption = ref({})
  const elderTagOption = ref({})

  const loadDashboard = () => {
    dashboardApi.getDashboard().then(result => {
      dashboard.value = result.data
      //根据后端数据生成各图表的配置项
      weekTaskOption.value = buildWeekTaskOption(result.data.weekTaskList || [])
      todayTaskStatusOption.value = buildPieOption('今日任务状态', result.data.todayTaskStatusList || [])
      contractTypeOption.value = buildPieOption('合同类型', result.data.contractTypeList || [])
      elderTagOption.value = buildElderTagOption(result.data.elderTagList || [])
    })
  }
  loadDashboard()

  // ============ 图表配置构建 ============

  //近7天护理任务柱状图配置：每天的待执行/已完成/已跳过三根柱子
  const buildWeekTaskOption = (weekTaskList) => {
    return {
      tooltip: {trigger: 'axis'},
      legend: {data: ['待执行', '已完成', '已跳过'], top: 0},
      grid: {left: 40, right: 20, top: 56, bottom: 30},
      xAxis: {type: 'category', data: weekTaskList.map(item => item.date)},
      yAxis: {type: 'value', minInterval: 1},
      series: [
        {name: '待执行', type: 'bar', data: weekTaskList.map(item => item.pendingCount), itemStyle: {color: '#E6A23C'}},
        {name: '已完成', type: 'bar', data: weekTaskList.map(item => item.completedCount), itemStyle: {color: '#67C23A'}},
        {name: '已跳过', type: 'bar', data: weekTaskList.map(item => item.skippedCount), itemStyle: {color: '#909399'}}
      ]
    }
  }

  //通用饼图配置：今日任务状态、合同类型两个饼图共用这一个构建方法，避免重复代码
  const buildPieOption = (seriesName, nameValueList) => {
    return {
      tooltip: {trigger: 'item', formatter: '{b}：{c}（{d}%）'},
      legend: {bottom: 0},
      series: [
        {
          name: seriesName,
          type: 'pie',
          radius: ['40%', '65%'],
          center: ['50%', '45%'],
          label: {formatter: '{b}\n{c}'},
          data: nameValueList
        }
      ]
    }
  }

  //老人标签条形图配置：横向柱状，按数量从大到小排列（后端已排好序）
  const buildElderTagOption = (elderTagList) => {
    return {
      tooltip: {trigger: 'axis', axisPointer: {type: 'shadow'}},
      grid: {left: 80, right: 30, top: 20, bottom: 30},
      xAxis: {type: 'value', minInterval: 1},
      yAxis: {type: 'category', data: elderTagList.map(item => item.name)},
      series: [
        {name: '老人数量', type: 'bar', data: elderTagList.map(item => item.value), itemStyle: {color: '#409EFF'}, barMaxWidth: 24}
      ]
    }
  }

  // ============ 顶部问候语 ============
  // 根据当前小时段返回对应的问候语
  const greeting = () => {
    const hour = new Date().getHours()
    if (hour < 6) return '夜深了'
    if (hour < 12) return '上午好'
    if (hour < 14) return '中午好'
    if (hour < 18) return '下午好'
    return '晚上好'
  }

  // 格式化为 YYYY年M月D日 空格 星期X
  const today = () => {
    const d = new Date()
    const weeks = ['日', '一', '二', '三', '四', '五', '六']
    return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weeks[d.getDay()]}`
  }
</script>

<template>
  <div class="home">
    <!-- ① 顶部欢迎横幅 -->
    <el-card class="welcome banner" shadow="never">
      <div class="welcome-body">
        <div class="welcome-text">
          <h2 class="welcome-title">{{ greeting() }}，{{ userInfoStore.user.name }}</h2>
          <p class="welcome-desc">欢迎回到智慧社区养老管理系统！</p>
        </div>
        <div class="welcome-date">
          <el-icon><Timer/></el-icon>
          <span>{{ today() }}</span>
        </div>
      </div>
    </el-card>

    <!-- ② 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" v-for="(item, i) in [
        {label: '老人总数',       value: dashboard.elderCount ?? 0,          icon: UserFilled,   color: '#409EFF'},
        {label: '合同总数',       value: dashboard.contractCount ?? 0,       icon: Document,     color: '#67C23A'},
        {label: '用户总数',       value: dashboard.userCount ?? 0,           icon: User,         color: '#E6A23C'},
        {label: '今日待执行任务', value: dashboard.todayPendingTaskCount ?? 0, icon: CollectionTag, color: '#F56C6C'},
      ]" :key="i">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-body">
            <div class="stat-icon" :style="{backgroundColor: item.color}">
              <el-icon :size="26"><component :is="item.icon"/></el-icon>
            </div>
            <div class="stat-info">
              <el-statistic :value="item.value"/>
              <span class="stat-label">{{ item.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ③ 数据图表：护理任务两张图 -->
    <el-row :gutter="16">
      <el-col :xs="24" :md="14">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>近7天护理任务完成情况</span>
            </div>
          </template>
          <EChart :option="weekTaskOption" height="300px"/>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="10">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>今日护理任务状态</span>
            </div>
          </template>
          <EChart :option="todayTaskStatusOption" height="300px"/>
        </el-card>
      </el-col>
    </el-row>

    <!-- ④ 数据图表：合同与标签两张图 -->
    <el-row :gutter="16">
      <el-col :xs="24" :md="10">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>合同类型分布</span>
            </div>
          </template>
          <EChart :option="contractTypeOption" height="300px"/>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="14">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>老人标签分布</span>
            </div>
          </template>
          <EChart :option="elderTagOption" height="300px"/>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
  .home {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  /* ---------- ① 欢迎横幅 ---------- */
  .welcome {
    background: linear-gradient(120deg, #409eff 0%, #53a8ff 55%, #409eff 100%);
    border: none;
    color: #fff;
    border-radius: 10px;
  }
  .welcome-body {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 12px;
  }
  .welcome-title {
    margin: 0 0 6px;
    font-size: 22px;
    color: #fff;
  }
  .welcome-desc {
    margin: 0;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.85);
  }
  .welcome-date {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.9);
  }

  /* ---------- ② 统计卡片 ---------- */
  .stat-row {
    margin-bottom: 0;
  }
  .stat-card {
    border-radius: 10px;
  }
  .stat-body {
    display: flex;
    align-items: center;
    gap: 14px;
  }
  .stat-icon {
    width: 52px;
    height: 52px;
    border-radius: 10px;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .stat-info {
    display: flex;
    flex-direction: column;
  }
  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 2px;
  }

  /* ---------- ③ 面板 ---------- */
  .panel {
    border-radius: 10px;
  }
  .panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-weight: 600;
  }
</style>