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
  import {useUserInfoStore} from '@/store/userInfo.js'
  import announcementApi from '@/api/announcement.js'

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

  // ================== 对象（公告） ==================

  // 最近公告列表（首页通知条取第一条，弹层展示全部4条）
  const noticeList = ref([])
  // 是否显示最近公告弹层
  const showNotice = ref(false)

  // ================== 下拉数据 ==================

  // 加载最近公告（已发布，最新4条）
  const loadNoticeList = () => {
    announcementApi.list({page: 1, limit: 4}).then(result => {
      noticeList.value = result.data.records
    })
  }
  loadNoticeList()

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

  //公告展示日期（createTime 截取 MM-DD）
  const getNoticeDate = (createTime) => {
    return createTime ? createTime.slice(5, 10) : ''
  }

  //打开最近公告弹层
  const openNotice = () => {
    showNotice.value = true
  }

  //跳转公告列表页（全部公告）
  const goNoticeList = () => {
    showNotice.value = false
    router.push('/announcement')
  }

  //跳转公告详情页
  const goNoticeDetail = (row) => {
    showNotice.value = false
    router.push({path: '/announcementDetail', query: {id: row.id}})
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
      <!-- 功能入口 -->
      <div class="home-grid-card">
        <van-grid :column-num="3" :border="false">
          <van-grid-item v-for="item in gridItems" :key="item.path" @click="goPage(item.path)">
            <van-icon :name="item.icon" size="28" :color="item.color"/>
            <span class="grid-title">{{ item.title }}</span>
          </van-grid-item>
        </van-grid>
      </div>

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

      <!-- 公告通知条 -->
      <div class="home-notice" v-if="noticeList.length > 0" @click="openNotice">
        <van-icon name="volume-o" size="16"/>
        <span class="home-notice-text">{{ noticeList[0].title }}</span>
        <van-icon name="arrow" size="14"/>
      </div>
    </div>

    <!-- 最近公告弹层（最多4条，右上角可进入全部公告列表） -->
    <van-popup v-model:show="showNotice" position="bottom" round>
      <div class="notice-popup">
        <div class="notice-popup-header">
          <span class="notice-popup-title">最近公告</span>
          <span class="notice-popup-more" @click="goNoticeList">查看更多 <van-icon name="arrow"/></span>
        </div>
        <van-empty description="暂无公告" v-if="noticeList.length === 0"/>
        <div
            class="notice-item"
            v-for="row in noticeList"
            :key="row.id"
            @click="goNoticeDetail(row)"
        >
          <p class="notice-item-title">{{ row.title }}</p>
          <p class="notice-item-date">{{ getNoticeDate(row.createTime) }}</p>
        </div>
      </div>
    </van-popup>
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
    margin-top: 12px;
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

  /* 公告通知条 */
  .home-notice {
    margin-top: 12px;
    background-color: #E8F3FF;
    border-radius: 8px;
    padding: 10px 12px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #1989FA;
  }

  .home-notice-text {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .home-notice > .van-icon:last-child {
    flex-shrink: 0;
  }

  /* 最近公告弹层 */
  .notice-popup {
    padding: 20px 16px 24px;
    max-height: 60vh;
    overflow-y: auto;
  }

  .notice-popup-header {
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    margin-bottom: 4px;
  }

  .notice-popup-title {
    font-size: 16px;
    font-weight: bold;
  }

  .notice-popup-more {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    display: flex;
    align-items: center;
    gap: 2px;
    font-size: 13px;
    color: #1989FA;
  }

  .notice-item {
    padding: 14px 0;
    border-bottom: 1px solid #F0F0F0;
  }

  .notice-item:last-child {
    border-bottom: none;
  }

  .notice-item-title {
    font-size: 15px;
    color: #323233;
    line-height: 22px;
  }

  .notice-item-date {
    margin-top: 6px;
    font-size: 12px;
    color: #999;
  }
</style>
