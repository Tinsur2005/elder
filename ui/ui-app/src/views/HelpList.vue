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
  import helpApi from '@/api/help.js'
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

  //求助列表
  const list = ref([])
  //是否正在加载（显示加载动画）
  const loading = ref(true)

  // ================== 选项 ==================

  // 求助类型选项（0健康 1生活 2安全 3其他），type为van-tag的type
  const typeOptions = [
    {value: 0, label: '健康', type: 'success'},
    {value: 1, label: '生活', type: 'primary'},
    {value: 2, label: '安全', type: 'danger'},
    {value: 3, label: '其他', type: 'warning'},
  ]

  // 紧急程度选项（0普通 1紧急 2非常紧急），type为van-tag的type
  const urgencyOptions = [
    {value: 0, label: '普通', type: 'default'},
    {value: 1, label: '紧急', type: 'warning'},
    {value: 2, label: '非常紧急', type: 'danger'},
  ]

  // 状态选项（0未处理 1已处理 2已忽略），color为状态文字的颜色
  const statusOptions = [
    {value: 0, label: '未处理', color: '#FF976A'},
    {value: 1, label: '已处理', color: '#07C160'},
    {value: 2, label: '已忽略', color: '#999999'},
  ]

  // ================== 方法 ==================

  //加载数据
  const loadData = () => {
    // 老人端查自己的求助，家属端查当前选中老人的求助
    const elderId = isFamily.value ? userInfoStore.currentElderId : userInfoStore.user.id
    helpApi.list({elderId}).then(result => {
      list.value = result.data.records
      loading.value = false
    })
  }

  loadData()

  //根据选项获取展示文本
  const getLabel = (options, value) => {
    return options.find(option => option.value === value)?.label || '-'
  }

  //根据选项获取van-tag的type
  const getTagType = (options, value) => {
    return options.find(option => option.value === value)?.type || 'default'
  }

  //根据状态获取展示信息
  const getStatus = (value) => {
    return statusOptions.find(option => option.value === value) || statusOptions[0]
  }

  //展示日期（createTime 截取 yyyy-MM-dd）
  const getDate = (createTime) => {
    return createTime ? createTime.slice(0, 10) : ''
  }

  //跳转到发起求助页
  const goSubmit = () => {
    router.push('/helpSubmit')
  }
</script>

<template>
  <div class="help-list">
    <van-nav-bar :title="isFamily ? currentElder.realName + '的求助' : '我的求助'" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 加载中 -->
    <div class="page-loading" v-if="loading">
      <van-loading size="24" vertical>加载中...</van-loading>
    </div>

    <template v-else>
      <!-- 空状态 -->
      <van-empty description="暂无求助记录" v-if="list.length === 0"/>

      <!-- 求助列表 -->
      <div class="help-card" v-for="row in list" :key="row.id">
        <!-- 类型标签 + 紧急程度标签 + 右侧状态文字 -->
        <div class="help-top">
          <van-tag :type="getTagType(typeOptions, row.type)">{{ getLabel(typeOptions, row.type) }}</van-tag>
          <van-tag :type="getTagType(urgencyOptions, row.urgency)" class="help-urgency">{{ getLabel(urgencyOptions, row.urgency) }}</van-tag>
          <span class="help-status" :style="{color: getStatus(row.status).color}">{{ getStatus(row.status).label }}</span>
        </div>
        <!-- 求助内容 -->
        <p class="help-content">{{ row.content }}</p>
        <!-- 处理结果（已处理时展示） -->
        <div class="help-result" v-if="row.result">
          <van-icon name="comment-o"/>
          <span>{{ row.result }}</span>
        </div>
        <!-- 底部：求助时间 + 处理人/处理时间 -->
        <div class="help-footer">
          <span><van-icon name="underway-o"/><span>{{ getDate(row.createTime) }}</span></span>
          <span v-if="row.handleTime"><van-icon name="manager-o"/><span>{{ row.handlerName }} · {{ getDate(row.handleTime) }}</span></span>
        </div>
      </div>
    </template>

    <!-- 底部发起求助按钮 -->
    <div class="submit-bar">
      <van-button round block type="primary" @click="goSubmit">发起求助</van-button>
    </div>
  </div>
</template>

<style scoped>
  .help-list {
    min-height: 100%;
    display: flex;
    flex-direction: column;
    padding: 12px 12px 20px;
  }

  /* 加载中 */
  .page-loading {
    display: flex;
    justify-content: center;
    padding: 60px 0;
  }

  /* 求助卡片 */
  .help-card {
    background-color: #FFFFFF;
    border-radius: 12px;
    padding: 14px 16px;
    margin-bottom: 10px;
  }

  .help-top {
    display: flex;
    align-items: center;
  }

  .help-urgency {
    margin-left: 8px;
  }

  .help-status {
    flex: 1;
    text-align: right;
    font-size: 13px;
  }

  .help-content {
    margin-top: 10px;
    font-size: 14px;
    color: #323233;
    line-height: 22px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
  }

  /* 处理结果 */
  .help-result {
    margin-top: 10px;
    background-color: #F5F6FA;
    border-radius: 8px;
    padding: 8px 10px;
    display: flex;
    align-items: flex-start;
    font-size: 13px;
    color: #666;
    line-height: 20px;
  }

  .help-result :deep(.van-icon) {
    margin-right: 6px;
    font-size: 14px;
    color: #1989FA;
    flex-shrink: 0;
    margin-top: 3px;
  }

  .help-footer {
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #F0F0F0;
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #999;
  }

  .help-footer span {
    display: flex;
    align-items: center;
  }

  .help-footer :deep(.van-icon) {
    margin-right: 4px;
    font-size: 13px;
    color: #C8C9CC;
  }

  /* 底部发起求助按钮（内容不满一屏时也固定在页面底部，沿用套餐详情页做法不脱离文档流） */
  .submit-bar {
    margin: auto 0 0;
    padding-top: 20px;
  }
</style>
