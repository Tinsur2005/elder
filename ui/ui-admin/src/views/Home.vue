<script setup>
  import elderApi from '@/api/elder.js'
  import contractApi from '@/api/contract.js'
  import userApi from '@/api/user.js'
  import tagsApi from '@/api/tags.js'
  import {ref} from 'vue'
  import {useRouter} from 'vue-router'
  import {useUserInfoStore} from '@/store/userInfo.js'
  import {Timer, User, Document, CollectionTag, Promotion, UserFilled, ArrowRight, SetUp} from '@element-plus/icons-vue'

  const router = useRouter()
  const userInfoStore = useUserInfoStore()

  // ============ 统计数据（进入页面时请求，取自各列表接口的 total，无需改后端） ============
  const stats = ref({
    elder: 0,    // 老人总数
    contract: 0, // 合同总数
    user: 0,     // 用户总数
    tag: 0       // 标签总数
  })

  // 供列表分页接口取 total 使用，limit 设为1只看总数
  const loadStats = () => {
    elderApi.list({page: 1, limit: 1}).then(result => {
      stats.value.elder = result.data.total
    })
    contractApi.list({page: 1, limit: 1}).then(result => {
      stats.value.contract = result.data.total
    })
    userApi.list({page: 1, limit: 1}).then(result => {
      stats.value.user = result.data.total
    })
    tagsApi.listAll().then(result => {
      stats.value.tag = result.data.length
    })
  }
  loadStats()

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

  // ============ 快捷入口 ============
  // 快捷入口列表：点击跳转到对应系统页面
  const quickLinks = [
    {name: '老人管理',                 icon: UserFilled,  path: '/elder'},
    {name: '合同管理',                 icon: Document,    path: '/contract'},
    {name: '标签管理',                 icon: CollectionTag, path: '/tag'},
    {name: '用户管理',                 icon: User,        path: '/user'},
  ]

  // 点击快捷入口跳转
  const go = (path) => {
    router.push(path)
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
        {label: '老人总数', value: stats.elder,    icon: UserFilled,   color: '#409EFF'},
        {label: '合同总数', value: stats.contract, icon: Document,     color: '#67C23A'},
        {label: '用户总数', value: stats.user,     icon: User,         color: '#E6A23C'},
        {label: '标签总数', value: stats.tag,      icon: CollectionTag, color: '#F56C6C'},
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

    <!-- ③ 快捷入口 + 系统信息 -->
    <el-row :gutter="16" class="bottom-row">
      <el-col :xs="24" :md="16">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>快捷入口</span>
              <el-icon><Promotion/></el-icon>
            </div>
          </template>
          <el-row :gutter="12">
            <el-col :xs="12" :sm="6" v-for="(item, i) in quickLinks" :key="i">
              <div class="quick-item" @click="go(item.path)">
                <div class="quick-icon">
                  <el-icon :size="22"><component :is="item.icon"/></el-icon>
                </div>
                <span class="quick-name">{{ item.name }}</span>
                <el-icon class="quick-arrow"><ArrowRight/></el-icon>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>系统信息</span>
              <el-icon><SetUp/></el-icon>
            </div>
          </template>
          <el-descriptions :column="1" class="sys-desc">
            <el-descriptions-item label="系统名称">智慧社区养老管理系统</el-descriptions-item>
            <el-descriptions-item label="当前版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="技术栈">Spring Boot + Vue 3</el-descriptions-item>
            <el-descriptions-item label="运行状态">正常</el-descriptions-item>
          </el-descriptions>
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
  .quick-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 14px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.2s;
    margin-bottom: 12px;
  }
  .quick-item:hover {
    border-color: var(--el-color-primary);
    color: var(--el-color-primary);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.12);
    transform: translateY(-2px);
  }
  .quick-icon {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    background: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .quick-name {
    flex: 1;
    font-size: 14px;
    color: #303133;
  }
  .quick-arrow {
    color: #c0c4cc;
  }
  .sys-desc {
    margin-top: 4px;
  }
</style>