<script setup>
  import {computed, ref} from 'vue'
  import {showConfirmDialog, showSuccessToast, showToast} from 'vant'
  import {useRouter} from 'vue-router'
  import examAppointmentApi from '@/api/examAppointment.js'
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

  //体检记录列表
  const list = ref([])

  // ================== 选项 ==================

  // 状态选项（状态：0待体检 1体检中 2已完成 3已取消 4已过期），color 为卡片右侧状态文字的颜色
  const statusOptions = [
    {value: 0, label: '待体检', color: '#1989FA'},
    {value: 1, label: '体检中', color: '#FF976A'},
    {value: 2, label: '已完成', color: '#07C160'},
    {value: 3, label: '已取消', color: '#999999'},
    {value: 4, label: '已过期', color: '#999999'},
  ]

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    // 老人端查自己的体检记录，家属端查当前选中老人的体检记录
    const elderId = isFamily.value ? userInfoStore.currentElderId : userInfoStore.user.id
    examAppointmentApi.list({elderId}).then(result => {
      list.value = result.data.records
    })
  }

  loadData()

  //根据状态获取展示信息
  const getStatus = (value) => {
    return statusOptions.find(option => option.value === value) || statusOptions[3]
  }

  //跳转到预约体检页面
  const goBooking = () => {
    if (isFamily.value && !userInfoStore.currentElderId) {
      showToast('请先在首页选择要体检的老人')
      return
    }
    router.push('/examBooking')
  }

  //跳转到体检记录详情
  const goDetail = (row) => {
    router.push({path: '/examRecordDetail', query: {id: row.id}})
  }

  //取消预约（待体检/体检中 → 已取消）
  const cancel = (row) => {
    showConfirmDialog({
      title: '提示',
      message: `确认取消【${row.packageName}】的体检预约么?`,
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    }).then(() => {
      examAppointmentApi.cancel(row.id).then(result => {
        if (result.code === 1) {
          showSuccessToast(result.msg)
          loadData()
        } else {
          showToast(result.msg)
        }
      })
    }).catch(() => {
      //取消操作
    })
  }
</script>

<template>
  <div class="exam">
    <!-- 页面标题 -->
    <van-nav-bar :title="isFamily ? currentElder.realName + '的体检' : '我的体检'" :fixed="true" placeholder/>

    <!-- 预约体检入口 -->
    <div class="exam-booking-entry" @click="goBooking">
      <div class="exam-booking-left">
        <van-icon name="calendar-o" size="24" color="#1989FA"/>
        <div class="exam-booking-info">
          <h4>预约体检</h4>
          <p>选择合适的体检套餐，按预约时间前来体检</p>
        </div>
      </div>
      <van-icon name="arrow" color="#C8C9CC"/>
    </div>

    <!-- 体检记录列表 -->
    <div class="exam-records">
      <div class="exam-records-title">体检记录</div>
      <!-- 空状态 -->
      <van-empty description="暂无体检记录" v-if="list.length === 0"/>
      <div
          class="record-card"
          v-for="row in list"
          :key="row.id"
          @click="goDetail(row)"
      >
        <!-- 类别标签 + 套餐名称 + 右侧状态文字 -->
        <div class="record-top">
          <van-tag type="primary">体检</van-tag>
          <span class="record-name">{{ row.packageName }}</span>
          <span class="record-status" :style="{color: getStatus(row.status).color}">{{ getStatus(row.status).label }}</span>
        </div>
        <!-- 摘要信息行（灰色小图标 + 文本） -->
        <div class="record-info">
          <p><van-icon name="manager-o"/><span>{{ row.elderName }}</span></p>
          <p><van-icon name="calendar-o"/><span>{{ row.appointmentDate }} {{ row.appointmentTime }}</span></p>
        </div>
        <!-- 底部：红色价格 + 描边胶囊按钮 -->
        <div class="record-footer">
          <span class="record-price">￥{{ row.price }}</span>
          <van-button
              v-if="row.status === 0 || row.status === 1"
              size="small"
              round
              plain
              color="#EE0A24"
              @click.stop="cancel(row)"
          >取消预约</van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .exam {
    min-height: 100vh;
    padding: 12px 12px 20px;
  }

  /* 预约体检入口 */
  .exam-booking-entry {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .exam-booking-left {
    display: flex;
    align-items: center;
  }

  .exam-booking-info {
    margin-left: 12px;
  }

  .exam-booking-info h4 {
    font-size: 15px;
  }

  .exam-booking-info p {
    margin-top: 2px;
    font-size: 12px;
    color: #999;
  }

  /* 体检记录 */
  .exam-records {
    margin-top: 12px;
  }

  .exam-records-title {
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 8px;
  }

  /* 体检记录卡片：类别标签 + 名称 + 状态文字 + 图标信息行 + 价格/按钮 */
  .record-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 14px 16px;
    margin-bottom: 10px;
  }

  .record-top {
    display: flex;
    align-items: center;
  }

  .record-name {
    flex: 1;
    margin-left: 8px;
    font-size: 15px;
    font-weight: bold;
  }

  .record-status {
    font-size: 13px;
  }

  .record-info {
    margin-top: 10px;
  }

  .record-info p {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #666;
    line-height: 24px;
  }

  .record-info :deep(.van-icon) {
    margin-right: 6px;
    font-size: 14px;
    color: #C8C9CC;
  }

  .record-footer {
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #F0F0F0;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .record-price {
    font-size: 18px;
    font-weight: bold;
    color: #EE0A24;
  }
</style>