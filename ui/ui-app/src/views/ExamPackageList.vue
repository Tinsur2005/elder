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
  import examPackageApi from '@/api/examPackage.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const userInfoStore = useUserInfoStore()
  const router = useRouter()

  // ================== 对象 ==================

  //是否为家属登录
  const isFamily = computed(() => userInfoStore.userType === 'family')
  // 家属代约的目标老人：老人登录是自己，家属登录是当前选中的绑定老人
  const currentElder = computed(() => {
    if (isFamily.value) {
      return userInfoStore.elders.find(item => item.id === userInfoStore.currentElderId) || {}
    }
    return userInfoStore.user
  })

  //体检套餐列表
  const packageList = ref([])
  //是否正在加载（显示加载动画）
  const loading = ref(true)

  // ================== 变量 ==================

  //当前页码
  const currentPage = ref(1)
  //每页展示的套餐数量
  const pageSize = 10
  //总页数
  const pageCount = computed(() => Math.ceil(packageList.value.length / pageSize))
  //当前页展示的套餐
  const displayList = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    return packageList.value.slice(start, start + pageSize)
  })

  // ================== 下拉数据 ==================

  // 体检套餐选项：进入页面一次加载全部上架状态套餐
  const loadPackageList = () => {
    examPackageApi.listAll().then(result => {
      packageList.value = result.data
      loading.value = false
    })
  }
  loadPackageList()

  // ================== 方法 ==================

  //跳转套餐详情页
  const goPackageDetail = (item) => {
    router.push({path: '/examPackageDetail', query: {id: item.id}})
  }
</script>

<template>
  <div class="package-list">
    <van-nav-bar title="体检预约" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 家属代约提示 -->
    <van-notice-bar left-icon="info-o" :scrollable="false" v-if="isFamily">
      正在为老人【{{ currentElder.realName }}】代约体检，进入套餐详情后可提交预约
    </van-notice-bar>

    <!-- 加载中 -->
    <div class="page-loading" v-if="loading">
      <van-loading size="24" vertical>加载中...</van-loading>
    </div>

    <template v-else>
      <!-- 套餐卡片列表 -->
      <div class="package-list-body" v-if="packageList.length > 0">
        <div class="package-card" v-for="item in displayList" :key="item.id" @click="goPackageDetail(item)">
          <van-image class="package-cover" width="86" height="64" radius="8" fit="cover" :src="item.image">
            <template #loading>
              <div class="package-cover-placeholder"><van-icon name="photo-o" size="22" color="#CCCCCC"/></div>
            </template>
            <template #error>
              <div class="package-cover-placeholder"><van-icon name="photo-o" size="22" color="#CCCCCC"/></div>
            </template>
          </van-image>
          <div class="package-info">
            <p class="package-name">{{ item.name }}</p>
            <p class="package-count">包含 {{ item.itemCount }} 个体检项目</p>
            <p class="package-price">￥{{ item.price }}</p>
          </div>
          <van-icon name="arrow" size="14" color="#CCCCCC"/>
        </div>
      </div>

      <!-- 暂无套餐 -->
      <van-empty description="暂无体检套餐" v-else/>

      <!-- 分页 -->
      <div class="package-pagination" v-if="pageCount > 1">
        <van-pagination v-model:current-page="currentPage" :total-items="packageList.length" :items-per-page="pageSize"/>
      </div>
    </template>
  </div>
</template>

<style scoped>
  .package-list {
    min-height: 100%;
    padding: 0 0 20px;
  }

  /* 加载中 */
  .page-loading {
    display: flex;
    justify-content: center;
    padding: 60px 0;
  }

  .package-list-body {
    padding: 12px 12px 0;
  }

  /* 套餐卡片 */
  .package-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 10px;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .package-cover {
    flex-shrink: 0;
  }

  .package-cover-placeholder {
    width: 86px;
    height: 64px;
    background-color: #F5F6FA;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .package-info {
    flex: 1;
    min-width: 0;
  }

  .package-name {
    font-size: 15px;
    font-weight: bold;
    color: #323233;
    line-height: 21px;
  }

  .package-count {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
  }

  .package-price {
    margin-top: 4px;
    font-size: 16px;
    font-weight: bold;
    color: #EE0A24;
  }

  /* 分页 */
  .package-pagination {
    margin-top: 16px;
  }
</style>