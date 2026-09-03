<script setup>
  import {computed} from 'vue'
  import {showConfirmDialog, showToast} from 'vant'
  import {useRouter} from 'vue-router'
  import {useTokenStore} from '@/store/token.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const tokenStore = useTokenStore()
  const userInfoStore = useUserInfoStore()
  const router = useRouter()

  // ================== 对象 ==================

  // 当前登录用户（家属端展示"与老人的关系"，老人端展示"老人标注"）
  const user = computed(() => userInfoStore.user)
  const isFamily = computed(() => userInfoStore.userType === 'family')

  // 我的页面菜单
  const menus = [
    {title: '个人信息', icon: 'user-circle-o', path: '/elderInfo'},
    {title: '我的合同', icon: 'bill-o', path: '/contract'}
  ]

  // ================== 方法 ==================

  //退出登录
  const logout = () => {
    showConfirmDialog({
      title: '提示',
      message: '确认退出登录么?',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    }).then(() => {
      tokenStore.removeToken()
      userInfoStore.removeUserInfo()
      userInfoStore.removeUserType()
      userInfoStore.removeElders()
      userInfoStore.removeCurrentElderId()
      showToast('已退出登录')
      router.push('/login')
    }).catch(() => {
      //取消退出
    })
  }
</script>

<template>
  <div class="profile">
    <van-nav-bar title="我的" :fixed="true" placeholder/>

    <!-- 用户信息卡 -->
    <div class="profile-header">
      <div class="profile-avatar">
        <van-icon name="manager" size="34" color="#FFFFFF"/>
      </div>
      <div class="profile-user-info">
        <h3>{{ user.realName }}</h3>
        <p>{{ isFamily ? user.relation : '老人' }}</p>
      </div>
    </div>

    <!-- 家属：绑定老人列表 -->
    <div class="profile-card" v-if="isFamily && userInfoStore.elders.length > 0">
      <van-cell
          v-for="elder in userInfoStore.elders"
          :key="elder.id"
          :title="elder.realName"
          :label="elder.phone"
          icon="user-o"
          is-link
          @click="router.push({path: '/elderInfo'})"
      />
    </div>

    <!-- 功能菜单 -->
    <div class="profile-card">
      <van-cell
          v-for="menu in menus"
          :key="menu.path"
          :title="menu.title"
          :icon="menu.icon"
          is-link
          :to="menu.path"
      />
    </div>

    <!-- 退出登录 -->
    <div class="profile-logout">
      <van-button round block plain color="#EE0A24" @click="logout">退出登录</van-button>
    </div>
  </div>
</template>

<style scoped>
  .profile {
    min-height: 100vh;
    padding: 0 0 20px;
  }

  /* 用户信息卡（蓝色渐变，与首页头部同款配色） */
  .profile-header {
    background: linear-gradient(135deg, #1989FA 0%, #5BA5FA 100%);
    border-radius: 12px;
    margin: 12px 12px 0;
    padding: 26px 20px;
    display: flex;
    align-items: center;
  }

  .profile-avatar {
    width: 62px;
    height: 62px;
    border: 2px solid rgba(255, 255, 255, 0.8);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .profile-user-info {
    margin-left: 16px;
    color: #FFFFFF;
  }

  .profile-user-info h3 {
    font-size: 20px;
    font-weight: bold;
  }

  .profile-user-info p {
    margin-top: 6px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.85);
  }

  /* 白卡列表（菜单/绑定老人） */
  .profile-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    margin: 12px 12px 0;
    overflow: hidden;
  }

  .profile-card :deep(.van-cell) {
    font-size: 15px;
    padding: 16px;
  }

  .profile-card :deep(.van-cell__left-icon) {
    font-size: 18px;
    color: #666;
    margin-right: 10px;
  }

  /* 退出登录（红色描边胶囊按钮） */
  .profile-logout {
    margin: 28px 16px 0;
  }
</style>