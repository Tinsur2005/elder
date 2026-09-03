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
  import contractApi from '@/api/contract.js'
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

  //合同列表
  const list = ref([])

  // ================== 选项 ==================

  // 合同类型选项（类型：0服务合同 1入住合同 2其他）
  const contractTypeOptions = [
    {value: 0, label: '服务合同'},
    {value: 1, label: '入住合同'},
    {value: 2, label: '其他'},
  ]

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    // 老人端查自己的合同，家属端查当前选中老人的合同
    const elderId = isFamily.value ? userInfoStore.currentElderId : userInfoStore.user.id
    contractApi.list({elderId}).then(result => {
      list.value = result.data.records
    })
  }

  loadData()

  //根据合同类型获取展示文本
  const getContractType = (value) => {
    return contractTypeOptions.find(option => option.value === value)?.label || '-'
  }

  //合同状态不由数据库存储，根据签订时间与过期时间推导（规则与后台管理端保持一致）
  //expireTime < now → 已过期；signTime > now → 待生效；无签订时间 → 待签订；其余 → 生效中
  const getContractStatus = (row) => {
    const now = new Date()
    if (!row.signTime) {
      return {label: '待签订', color: '#999999'}
    }
    if (row.expireTime && new Date(row.expireTime) < now) {
      return {label: '已过期', color: '#EE0A24'}
    }
    if (new Date(row.signTime) > now) {
      return {label: '待生效', color: '#1989FA'}
    }
    return {label: '生效中', color: '#07C160'}
  }

  //跳转到合同详情
  const goDetail = (row) => {
    router.push({path: '/contractDetail', query: {id: row.id}})
  }
</script>

<template>
  <div class="contract-list">
    <van-nav-bar :title="isFamily ? currentElder.realName + '的合同' : '我的合同'" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 空状态 -->
    <van-empty description="暂无合同" v-if="list.length === 0"/>

    <!-- 合同列表 -->
    <div
        class="contract-card"
        v-for="row in list"
        :key="row.id"
        @click="goDetail(row)"
    >
      <!-- 类别标签 + 合同名称 + 右侧状态文字 -->
      <div class="contract-top">
        <van-tag type="primary">合同</van-tag>
        <span class="contract-name">{{ row.contractName }}</span>
        <span class="contract-status" :style="{color: getContractStatus(row).color}">{{ getContractStatus(row).label }}</span>
      </div>
      <!-- 摘要信息行（灰色小图标 + 文本） -->
      <div class="contract-info">
        <p><van-icon name="description-o"/><span>{{ getContractType(row.contractType) }}</span></p>
        <p><van-icon name="bookmark-o"/><span>{{ row.contractNo }}</span></p>
        <p><van-icon name="calendar-o"/><span>{{ row.signTime ? row.signTime.slice(0, 10) : '-' }} 至 {{ row.expireTime ? row.expireTime.slice(0, 10) : '-' }}</span></p>
      </div>
      <!-- 底部：查看详情 -->
      <div class="contract-footer">
        <span>查看合同详情</span>
        <van-icon name="arrow" color="#1989FA"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .contract-list {
    min-height: 100vh;
    padding: 12px 12px 20px;
  }

  /* 合同卡片：类别标签 + 名称 + 状态文字 + 图标信息行 */
  .contract-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 14px 16px;
    margin-bottom: 10px;
  }

  .contract-top {
    display: flex;
    align-items: center;
  }

  .contract-name {
    flex: 1;
    margin-left: 8px;
    font-size: 15px;
    font-weight: bold;
  }

  .contract-status {
    font-size: 13px;
  }

  .contract-info {
    margin-top: 10px;
  }

  .contract-info p {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #666;
    line-height: 24px;
  }

  .contract-info :deep(.van-icon) {
    margin-right: 6px;
    font-size: 14px;
    color: #C8C9CC;
  }

  .contract-footer {
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
