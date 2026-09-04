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
  import {useRoute, useRouter} from 'vue-router'
  import examPackageApi from '@/api/examPackage.js'
  import examItemApi from '@/api/examItem.js'
  import examAppointmentApi from '@/api/examAppointment.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const userInfoStore = useUserInfoStore()
  const route = useRoute()
  const router = useRouter()

  // ================== 对象 ==================

  // 家属代约的目标老人：老人登录是自己，家属登录是当前选中的绑定老人
  const currentElder = computed(() => {
    if (userInfoStore.userType === 'family') {
      return userInfoStore.elders.find(item => item.id === userInfoStore.currentElderId) || {}
    }
    return userInfoStore.user
  })

  //套餐对象
  const examPackage = ref({})
  //套餐包含的体检项目列表，价格和说明由体检项目列表映射
  const packageItems = ref([])
  //是否正在加载（显示加载动画）
  const loading = ref(true)

  // ================== 下拉数据 ==================

  // 体检项目列表：一次加载全部，用于把套餐包含的项目id映射成名称价格说明展示
  const examItemList = ref([])

  //加载套餐详情和套餐包含的体检项目
  const loadDetail = () => {
    Promise.all([
      examPackageApi.listAll().then(result => {
        examPackage.value = result.data.find(item => item.id === route.query.id) || {}
      }),
      examItemApi.listAll().then(result => {
        examItemList.value = result.data
      }),
      examPackageApi.getPackageItemsById(route.query.id).then(result => {
        packageItems.value = result.data
      })
    ]).then(() => {
      packageItems.value = packageItems.value.map(packageItem => {
        const examItem = examItemList.value.find(item => item.id === packageItem.examItemId) || {}
        return {
          ...packageItem,
          itemName: examItem.name || '',
          itemPrice: examItem.price,
          itemDescription: examItem.description || ''
        }
      })
      loading.value = false
    })
  }
  loadDetail()

  // ================== 变量 ==================

  //是否显示预约面板
  const showBooking = ref(false)
  //选中的预约日期
  const appointmentDate = ref('')
  //选中的预约时间段
  const appointmentTime = ref('')
  //日期选择弹窗
  const showCalendar = ref(false)
  //时间段选择弹窗
  const showTimePicker = ref(false)
  //日期选择范围：从今天起三个月内
  const minDate = new Date()
  const maxDate = new Date(minDate.getFullYear(), minDate.getMonth() + 3, minDate.getDate())

  // 可选的预约时间段（text 为展示文本，value 与后端 appointmentTime 格式 HH:mm:ss 保持一致）
  const timeColumns = [
    {text: '08:00', value: '08:00:00'},
    {text: '09:00', value: '09:00:00'},
    {text: '10:00', value: '10:00:00'},
    {text: '14:00', value: '14:00:00'},
    {text: '15:00', value: '15:00:00'},
    {text: '16:00', value: '16:00:00'},
  ]

  // ================== 方法 ==================

  //打开预约面板并清空上次选择
  const openBooking = () => {
    appointmentDate.value = ''
    appointmentTime.value = ''
    showBooking.value = true
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
    if (!appointmentDate.value) {
      showToast('请选择预约日期')
      return
    }
    if (!appointmentTime.value) {
      showToast('请选择预约时间段')
      return
    }
    showConfirmDialog({
      title: '确认预约',
      message: `老人：${currentElder.value.realName}\n套餐：${examPackage.value.name}（￥${examPackage.value.price}）\n时间：${appointmentDate.value} ${appointmentTime.value.slice(0, 5)}`,
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    }).then(() => {
      examAppointmentApi.add({
        elderId: currentElder.value.id,
        packageId: examPackage.value.id,
        appointmentDate: appointmentDate.value,
        appointmentTime: appointmentTime.value
      }).then(result => {
        if (result.code === 1) {
          showBooking.value = false
          showSuccessToast(result.msg)
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
  <div class="package-detail">
    <van-nav-bar title="套餐详情" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 加载中 -->
    <div class="page-loading" v-if="loading">
      <van-loading size="24" vertical>加载中...</van-loading>
    </div>

    <!-- 套餐不存在 -->
    <van-empty description="套餐不存在或已下架" v-else-if="!examPackage.id"/>

    <template v-else>
      <!-- 套餐图片 -->
      <van-image class="package-image" width="100%" height="210" fit="cover" :src="examPackage.image">
        <template #loading>
          <div class="package-image-placeholder"><van-icon name="photo-o" size="40" color="#CCCCCC"/></div>
        </template>
        <template #error>
          <div class="package-image-placeholder"><van-icon name="photo-o" size="40" color="#CCCCCC"/></div>
        </template>
      </van-image>

      <!-- 套餐信息 -->
      <div class="package-card">
        <div class="package-top">
          <span class="package-name">{{ examPackage.name }}</span>
          <span class="package-price">￥{{ examPackage.price }}</span>
        </div>
        <p class="package-desc">{{ examPackage.description }}</p>
      </div>

      <!-- 套餐项目 -->
      <div class="items-card">
        <div class="items-title">套餐项目</div>
        <div class="item-row" v-for="packageItem in packageItems" :key="packageItem.id">
          <div class="item-top">
            <span class="item-name">{{ packageItem.itemName }}</span>
            <span class="item-price">￥{{ packageItem.itemPrice }}</span>
          </div>
          <p class="item-desc">{{ packageItem.itemDescription }}</p>
        </div>
      </div>

      <!-- 底部预约按钮 -->
      <div class="booking-submit">
        <van-button round block type="primary" @click="openBooking">预约此套餐</van-button>
      </div>
    </template>

    <!-- 预约面板 -->
    <van-popup v-model:show="showBooking" position="bottom" round>
      <div class="booking-panel">
        <div class="booking-panel-title">确认预约</div>
        <van-cell-group inset>
          <van-cell title="预约日期" :value="appointmentDate || '请选择'" is-link @click="showCalendar = true"/>
          <van-cell title="预约时间段" :value="appointmentTime ? appointmentTime.slice(0, 5) : '请选择'" is-link @click="showTimePicker = true"/>
        </van-cell-group>
        <div class="booking-panel-submit">
          <van-button round block type="primary" @click="submit">提交预约</van-button>
        </div>
      </div>
    </van-popup>

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
  .package-detail {
    min-height: 100%;
    display: flex;
    flex-direction: column;
    padding: 0 0 20px;
  }

  /* 加载中 */
  .page-loading {
    display: flex;
    justify-content: center;
    padding: 60px 0;
  }

  /* 套餐图片 */
  .package-image {
    display: block;
  }

  .package-image-placeholder {
    width: 100%;
    height: 210px;
    background-color: #F5F6FA;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  /* 套餐信息 */
  .package-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    margin: 12px 12px 0;
    padding: 16px;
  }

  .package-top {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
  }

  .package-name {
    font-size: 17px;
    font-weight: bold;
    color: #323233;
  }

  .package-price {
    font-size: 18px;
    font-weight: bold;
    color: #EE0A24;
  }

  .package-desc {
    margin-top: 10px;
    font-size: 13px;
    color: #666;
    line-height: 20px;
  }

  /* 套餐项目 */
  .items-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    margin: 12px 12px 0;
    padding: 16px;
  }

  .items-title {
    font-size: 15px;
    font-weight: bold;
    color: #323233;
    margin-bottom: 4px;
  }

  .item-row {
    padding: 12px 0;
    border-bottom: 1px solid #F0F0F0;
  }

  .item-row:last-child {
    border-bottom: none;
  }

  .item-top {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
  }

  .item-name {
    font-size: 14px;
    color: #323233;
  }

  .item-price {
    font-size: 14px;
    font-weight: bold;
    color: #EE0A24;
  }

  .item-desc {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
    line-height: 18px;
  }

  /* 底部预约按钮（内容不满一屏时也固定在页面底部） */
  .booking-submit {
    margin: auto 16px 0;
    padding-top: 20px;
  }

  /* 预约面板 */
  .booking-panel {
    padding: 20px 0 24px;
  }

  .booking-panel-title {
    font-size: 16px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 16px;
  }

  .booking-panel-submit {
    margin: 20px 16px 0;
  }
</style>