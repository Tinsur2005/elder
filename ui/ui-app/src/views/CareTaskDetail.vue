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
  import careTaskApi from '@/api/careTask.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const userInfoStore = useUserInfoStore()
  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  //是否为家属登录
  const isFamily = computed(() => userInfoStore.userType === 'family')
  //护理任务对象
  const careTask = ref({})
  //是否正在加载（显示加载动画）
  const loading = ref(true)

  // ================== 选项 ==================

  // 任务状态选项（状态：0待执行 1已完成 2已跳过/取消），color 为状态文字的颜色
  const statusOptions = [
    {value: 0, label: '待执行', color: '#FF976A'},
    {value: 1, label: '已完成', color: '#07C160'},
    {value: 2, label: '已跳过', color: '#999999'},
  ]

  // ================== 下拉数据 ==================

  //加载护理任务详情
  const loadCareTask = () => {
    careTaskApi.selectById(route.query.id).then(result => {
      if (result.code === 1) {
        careTask.value = result.data
      }
      loading.value = false
    })
  }

  loadCareTask()

  // ================== 方法 ==================

  //根据状态获取展示信息
  const getStatus = (value) => {
    return statusOptions.find(option => option.value === value) || statusOptions[0]
  }
</script>

<template>
  <div class="task-detail">
    <van-nav-bar title="护理任务详情" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 加载中 -->
    <div class="page-loading" v-if="loading">
      <van-loading size="24" vertical>加载中...</van-loading>
    </div>

    <template v-else>
    <!-- 任务执行状态 -->
    <div class="detail-card">
      <div class="detail-top">
        <van-tag type="primary">护理</van-tag>
        <span class="detail-name">{{ careTask.careItemName }}</span>
        <span class="detail-status" :style="{color: getStatus(careTask.status).color}">{{ getStatus(careTask.status).label }}</span>
      </div>
      <div class="detail-info">
        <p><van-icon name="manager-o"/><span>老人：{{ careTask.elderName }}</span></p>
        <p><van-icon name="calendar-o"/><span>计划时间：{{ careTask.planExecuteDate }} {{ careTask.planExecuteTime }}</span></p>
        <p><van-icon name="user-o"/><span>执行护理员：{{ careTask.userName }}</span></p>
        <p v-if="careTask.actualExecuteTime"><van-icon name="clock-o"/><span>实际执行时间：{{ careTask.actualExecuteTime }}</span></p>
        <p v-if="careTask.executeResult"><van-icon name="checked"/><span>执行结果：{{ careTask.executeResult }}</span></p>
        <p v-if="careTask.remark"><van-icon name="comment-o"/><span>备注：{{ careTask.remark }}</span></p>
      </div>
    </div>

    <!-- 家属端提示 -->
    <van-notice-bar left-icon="info-o" :scrollable="false" v-if="isFamily">
      护理任务由护理人员执行并打卡，如有疑问请联系负责护理员
    </van-notice-bar>
    </template>
  </div>
</template>

<style scoped>
  .task-detail {
    min-height: 100vh;
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
</style>
