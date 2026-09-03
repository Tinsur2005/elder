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
  import {showConfirmDialog, showSuccessToast, showToast} from 'vant'
  import {useRouter} from 'vue-router'
  import examPackageApi from '@/api/examPackage.js'
  import examItemApi from '@/api/examItem.js'
  import examAppointmentApi from '@/api/examAppointment.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const userInfoStore = useUserInfoStore()
  const router = useRouter()

  // ================== 对象 ==================

  //是否为家属登录（家属代约时页面显示当前老人姓名）
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
  //选中的套餐id
  const packageId = ref(null)
  //选中的预约日期
  const appointmentDate = ref('')
  //选中的预约时间段
  const appointmentTime = ref('')

  // ================== 选项 ==================

  // 可选的预约时间段（text 为展示文本，value 与后端 appointmentTime 格式 HH:mm:ss 保持一致）
  const timeColumns = [
    {text: '08:00', value: '08:00:00'},
    {text: '09:00', value: '09:00:00'},
    {text: '10:00', value: '10:00:00'},
    {text: '14:00', value: '14:00:00'},
    {text: '15:00', value: '15:00:00'},
    {text: '16:00', value: '16:00:00'},
  ]

  // ================== 下拉数据 ==================

  // 体检套餐选项：进入页面一次加载全部上架状态套餐
  const loadPackageOptions = () => {
    examPackageApi.listAll().then(result => {
      packageList.value = result.data
    })
  }
  loadPackageOptions()

  // 体检项目列表：一次加载全部，用于把套餐包含的项目id映射成名称展示
  const examItemList = ref([])
  const loadExamItems = () => {
    examItemApi.listAll().then(result => {
      examItemList.value = result.data
    })
  }
  loadExamItems()

  // 套餐包含的体检项目（点选套餐后加载展示，项目名称由体检项目列表映射）
  const packageItems = ref([])
  const loadPackageItems = (id) => {
    examPackageApi.getPackageItemsById(id).then(result => {
      packageItems.value = result.data.map(packageItem => ({
        ...packageItem,
        itemName: examItemList.value.find(item => item.id === packageItem.examItemId)?.name || ''
      }))
    })
  }

  // ================== 变量 ==================

  //日期选择弹窗
  const showCalendar = ref(false)
  //时间段选择弹窗
  const showTimePicker = ref(false)
  //日期选择范围：从今天起三个月内
  const minDate = new Date()
  const maxDate = new Date(minDate.getFullYear(), minDate.getMonth() + 3, minDate.getDate())

  // ================== 方法 ==================

  //点选套餐
  const selectPackage = (item) => {
    packageId.value = item.id
    loadPackageItems(item.id)
  }

  //确认选择日期
  const onConfirmDate = (date) => {
    const pad = (num) => String(num).padStart(2, '0')
    appointmentDate.value = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
    showCalendar.value = false
  }

  //确认选择时间段（存 value，与后端 HH:mm:ss 格式一致；展示时截取 HH:mm）
  const onConfirmTime = ({selectedOptions}) => {
    appointmentTime.value = selectedOptions[0]?.value || ''
    showTimePicker.value = false
  }

  //提交预约
  const submit = () => {
    if (!packageId.value) {
      showToast('请选择体检套餐')
      return
    }
    if (!appointmentDate.value) {
      showToast('请选择预约日期')
      return
    }
    if (!appointmentTime.value) {
      showToast('请选择预约时间段')
      return
    }
    const examPackage = packageList.value.find(item => item.id === packageId.value)
    showConfirmDialog({
      title: '确认预约',
      message: `老人：${currentElder.value.realName}\n套餐：${examPackage.name}（￥${examPackage.price}）\n时间：${appointmentDate.value} ${appointmentTime.value.slice(0, 5)}`,
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    }).then(() => {
      examAppointmentApi.add({
        elderId: currentElder.value.id,
        packageId: packageId.value,
        appointmentDate: appointmentDate.value,
        appointmentTime: appointmentTime.value
      }).then(result => {
        if (result.code === 1) {
          showSuccessToast(result.msg)
          router.back()
        } else {
          showToast(result.msg)
        }
      })
    }).catch(() => {
      //取消提交
    })
  }
</script>

<template>
  <div class="booking">
    <van-nav-bar title="预约体检" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 家属代约提示 -->
    <van-notice-bar left-icon="info-o" :scrollable="false" v-if="isFamily">
      正在为老人【{{ currentElder.realName }}】代约体检，如需更换请返回首页切换老人
    </van-notice-bar>

    <!-- 选择体检套餐 -->
    <div class="booking-section">
      <div class="booking-section-title">选择体检套餐</div>
      <div
          class="package-card"
          :class="{'package-card-active': item.id === packageId}"
          v-for="item in packageList"
          :key="item.id"
          @click="selectPackage(item)"
      >
        <div class="package-top">
          <span class="package-name">{{ item.name }}</span>
          <span class="package-price">￥{{ item.price }}</span>
        </div>
        <p class="package-desc">{{ item.description }}</p>
        <p class="package-count">包含 {{ item.itemCount }} 个体检项目</p>
        <!-- 套餐包含的体检项目 -->
        <div class="package-items" v-if="item.id === packageId">
          <van-tag plain type="primary" v-for="packageItem in packageItems" :key="packageItem.examItemId">
            {{ packageItem.itemName }}
          </van-tag>
        </div>
      </div>
    </div>

    <!-- 选择预约时间 -->
    <div class="booking-section">
      <div class="booking-section-title">选择预约时间</div>
      <van-cell-group inset>
        <van-cell title="预约日期" :value="appointmentDate || '请选择'" is-link @click="showCalendar = true"/>
        <van-cell title="预约时间段" :value="appointmentTime ? appointmentTime.slice(0, 5) : '请选择'" is-link @click="showTimePicker = true"/>
      </van-cell-group>
    </div>

    <!-- 提交按钮 -->
    <div class="booking-submit">
      <van-button round block type="primary" @click="submit">提交预约</van-button>
    </div>

    <!-- 日期选择弹窗 -->
    <van-calendar
        v-model:show="showCalendar"
        :min-date="minDate"
        :max-date="maxDate"
        title="选择预约日期"
        @confirm="onConfirmDate"
    />

    <!-- 时间段选择弹窗 -->
    <van-popup v-model:show="showTimePicker" position="bottom" round>
      <van-picker
          title="选择预约时间段"
          :columns="timeColumns"
          @confirm="onConfirmTime"
          @cancel="showTimePicker = false"
      />
    </van-popup>
  </div>
</template>

<style scoped>
  .booking {
    min-height: 100vh;
    padding: 12px 0 20px;
  }

  .booking-section {
    margin-top: 4px;
  }

  .booking-section-title {
    font-size: 15px;
    font-weight: bold;
    margin: 12px 16px 8px;
  }

  /* 套餐卡片 */
  .package-card {
    background-color: #FFFFFF;
    border: 1px solid transparent;
    border-radius: 12px;
    padding: 14px 16px;
    margin: 0 12px 10px;
  }

  .package-card-active {
    border: 1px solid #1989FA;
    background-color: #E8F3FF;
  }

  .package-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .package-name {
    font-size: 15px;
    font-weight: bold;
  }

  .package-price {
    font-size: 16px;
    font-weight: bold;
    color: #EE0A24;
  }

  .package-desc {
    margin-top: 8px;
    font-size: 13px;
    color: #666;
    line-height: 20px;
  }

  .package-count {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
  }

  .package-items {
    margin-top: 10px;
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }

  /* 提交按钮 */
  .booking-submit {
    margin: 20px 16px 0;
  }
</style>
