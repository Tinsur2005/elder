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
    <!-- 用户信息卡 -->
    <div class="profile-header">
      <div class="profile-avatar">
        <van-icon name="user-o" size="26" color="#1989FA"/>
      </div>
      <div class="profile-user-info">
        <h3>{{ user.realName }}</h3>
        <p>{{ isFamily ? user.relation : '老人' }}</p>
      </div>
    </div>

    <!-- 家属：绑定老人列表 -->
    <van-cell-group inset class="profile-group" v-if="isFamily && userInfoStore.elders.length > 0">
      <van-cell title="绑定老人" :border="false"/>
      <van-cell
          v-for="elder in userInfoStore.elders"
          :key="elder.id"
          :title="elder.realName"
          :label="elder.phone"
          icon="user-o"
          is-link
          @click="router.push({path: '/elderInfo'})"
      />
    </van-cell-group>

    <!-- 功能菜单 -->
    <van-cell-group inset class="profile-group">
      <van-cell
          v-for="menu in menus"
          :key="menu.path"
          :title="menu.title"
          :icon="menu.icon"
          is-link
          :to="menu.path"
      />
    </van-cell-group>

    <!-- 退出登录 -->
    <div class="profile-logout">
      <van-button round block plain type="primary" @click="logout">退出登录</van-button>
    </div>
  </div>
</template>

<style scoped>
  .profile {
    min-height: 100vh;
    padding: 12px 0 20px;
  }

  .profile-header {
    background-color: #FFFFFF;
    border-radius: 12px;
    margin: 0 12px;
    padding: 24px 16px;
    display: flex;
    align-items: center;
  }

  .profile-avatar {
    width: 60px;
    height: 60px;
    background-color: #E8F3FF;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .profile-user-info {
    margin-left: 14px;
    color: #323233;
  }

  .profile-user-info h3 {
    font-size: 18px;
  }

  .profile-user-info p {
    margin-top: 4px;
    font-size: 13px;
    color: #999;
  }

  .profile-group {
    margin-top: 12px;
  }

  .profile-logout {
    margin: 24px 16px 0;
  }
</style>
