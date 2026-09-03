<script setup>
  import {computed} from 'vue'
  import {useRouter} from 'vue-router'
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

  // 是否为家属登录（家属首页展示老人卡片切换）
  const isFamily = computed(() => userInfoStore.userType === 'family')

  // ================== 选项 ==================

  // 功能入口（家属端文案与老人端略有区别），color 为入口图标的颜色
  const gridItems = computed(() => {
    if (isFamily.value) {
      return [
        {title: '查看资料', icon: 'user-circle-o', color: '#1989FA', path: '/elderInfo'},
        {title: '代约体检', icon: 'calendar-o', color: '#07C160', path: '/examBooking'},
        {title: '体检记录', icon: 'records', color: '#FF976A', path: '/exam'},
        {title: '护理计划', icon: 'orders-o', color: '#EE0A24', path: '/carePlan'},
        {title: '护理任务', icon: 'todo-list-o', color: '#1989FA', path: '/careTask'},
        {title: '合同查询', icon: 'bill-o', color: '#07C160', path: '/contract'}
      ]
    }
    return [
      {title: '体检预约', icon: 'calendar-o', color: '#1989FA', path: '/examBooking'},
      {title: '体检记录', icon: 'records', color: '#07C160', path: '/exam'},
      {title: '护理计划', icon: 'orders-o', color: '#FF976A', path: '/carePlan'},
      {title: '护理任务', icon: 'todo-list-o', color: '#EE0A24', path: '/careTask'},
      {title: '我的合同', icon: 'bill-o', color: '#1989FA', path: '/contract'},
      {title: '个人信息', icon: 'user-circle-o', color: '#07C160', path: '/elderInfo'}
    ]
  })

  // ================== 方法 ==================

  //根据出生日期计算年龄
  const getAge = (birthday) => {
    if (!birthday) {
      return ''
    }
    const birth = new Date(birthday)
    const now = new Date()
    let age = now.getFullYear() - birth.getFullYear()
    // 未过生日时年龄减一
    if (now.getMonth() < birth.getMonth() || (now.getMonth() === birth.getMonth() && now.getDate() < birth.getDate())) {
      age = age - 1
    }
    return age
  }

  //切换家属当前查看的老人
  const switchElder = (id) => {
    userInfoStore.setCurrentElderId(id)
  }

  //跳转到功能页面
  const goPage = (path) => {
    router.push(path)
  }
</script>

<template>
  <div class="home">
    <!-- 顶部蓝色问候区 -->
    <div class="home-header">
      <h3 class="home-title">您好，{{ currentElder.realName }}{{ isFamily ? '的家属' : '' }}</h3>
      <p class="home-subtitle" v-if="isFamily">当前查看：{{ currentElder.realName }}（{{ getAge(currentElder.birthday) }}岁）</p>
      <p class="home-subtitle" v-else>今天也要注意身体哦</p>
      <!-- 老人标注 -->
      <div class="home-tags" v-if="currentElder.tags && currentElder.tags.length > 0">
        <van-tag color="rgba(255, 255, 255, 0.25)" text-color="#FFFFFF" v-for="tag in currentElder.tags" :key="tag.id">{{ tag.name }}</van-tag>
      </div>
    </div>

    <div class="home-body">
      <!-- 家属：绑定老人卡片切换 -->
      <div class="elder-switch" v-if="isFamily && userInfoStore.elders.length > 0">
        <div class="section-title"><span class="section-bar"></span>切换查看老人</div>
        <div class="elder-cards">
          <div
              class="elder-card"
              :class="{'elder-card-active': elder.id === userInfoStore.currentElderId}"
              v-for="elder in userInfoStore.elders"
              :key="elder.id"
              @click="switchElder(elder.id)"
          >
            <div class="elder-card-avatar">
              <van-icon name="user-o" size="22" :color="elder.id === userInfoStore.currentElderId ? '#1989FA' : '#999'"/>
            </div>
            <p class="elder-card-name">{{ elder.realName }}</p>
            <p class="elder-card-age">{{ getAge(elder.birthday) }}岁</p>
          </div>
        </div>
      </div>

      <!-- 功能入口 -->
      <div class="home-grid-card">
        <van-grid :column-num="3" :border="false">
          <van-grid-item v-for="item in gridItems" :key="item.path" @click="goPage(item.path)">
            <van-icon :name="item.icon" size="28" :color="item.color"/>
            <span class="grid-title">{{ item.title }}</span>
          </van-grid-item>
        </van-grid>
      </div>

      <!-- 温馨提示 -->
      <div class="home-notice">
        <van-icon name="volume-o" size="16"/>
        <span>保持规律作息，适度锻炼；按时服药，定期体检。如需帮助请联系您的护理人员。</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .home {
    min-height: 100vh;
    padding-bottom: 20px;
  }

  /* 顶部蓝色问候区 */
  .home-header {
    background: linear-gradient(180deg, #1989FA 0%, #5BA5FA 100%);
    padding: 28px 16px 56px;
    color: #FFFFFF;
  }

  .home-title {
    font-size: 22px;
    font-weight: bold;
  }

  .home-subtitle {
    margin-top: 8px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.85);
  }

  .home-tags {
    margin-top: 12px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  /* 内容区上叠在蓝色头部上 */
  .home-body {
    padding: 0 12px;
    margin-top: -36px;
  }

  /* 小节标题（蓝色竖条 + 加粗文字） */
  .section-title {
    display: flex;
    align-items: center;
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 10px;
  }

  .section-bar {
    width: 4px;
    height: 14px;
    background-color: #1989FA;
    border-radius: 2px;
    margin-right: 6px;
  }

  /* 家属：老人卡片切换 */
  .elder-switch {
    margin-bottom: 12px;
  }

  .elder-cards {
    display: flex;
    gap: 10px;
    overflow-x: auto;
    padding-bottom: 4px;
  }

  .elder-card {
    background-color: #FFFFFF;
    border: 1px solid #EBEEF0;
    border-radius: 12px;
    padding: 14px 20px;
    text-align: center;
    flex-shrink: 0;
    min-width: 96px;
  }

  .elder-card-active {
    border: 1px solid #1989FA;
    background-color: #E8F3FF;
  }

  .elder-card-avatar {
    width: 40px;
    height: 40px;
    margin: 0 auto;
    background-color: #F5F6FA;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .elder-card-active .elder-card-avatar {
    background-color: #FFFFFF;
  }

  .elder-card-name {
    margin-top: 8px;
    font-size: 14px;
    font-weight: bold;
  }

  .elder-card-age {
    margin-top: 2px;
    font-size: 12px;
    color: #999;
  }

  /* 功能入口 */
  .home-grid-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 8px 0;
  }

  .grid-title {
    margin-top: 8px;
    font-size: 13px;
    color: #323233;
  }

  /* 温馨提示（浅蓝通知条） */
  .home-notice {
    margin-top: 12px;
    background-color: #E8F3FF;
    border-radius: 8px;
    padding: 10px 12px;
    display: flex;
    align-items: flex-start;
    gap: 8px;
    font-size: 13px;
    color: #1989FA;
    line-height: 20px;
  }

  .home-notice .van-icon {
    margin-top: 2px;
    flex-shrink: 0;
  }
</style>
