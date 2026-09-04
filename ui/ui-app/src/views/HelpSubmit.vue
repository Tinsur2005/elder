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
  import {showSuccessToast, showToast} from 'vant'
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

  // ================== 选项 ==================

  // 求助类型选项（0健康 1生活 2安全 3其他）
  const typeOptions = [
    {text: '健康', value: 0},
    {text: '生活', value: 1},
    {text: '安全', value: 2},
    {text: '其他', value: 3},
  ]

  // 紧急程度选项（0普通 1紧急 2非常紧急）
  const urgencyOptions = [
    {text: '普通', value: 0},
    {text: '紧急', value: 1},
    {text: '非常紧急', value: 2},
  ]

  // ================== 变量 ==================

  //表单数据
  const form = ref({
    type: 0,
    urgency: 0,
    content: ''
  })
  //是否显示求助类型选择弹窗
  const showTypePicker = ref(false)
  //是否显示紧急程度选择弹窗
  const showUrgencyPicker = ref(false)

  // ================== 方法 ==================

  //根据选项value获取展示文本
  const getLabel = (options, value) => {
    return options.find(option => option.value === value)?.text || '请选择'
  }

  //确认选择求助类型
  const onConfirmType = ({selectedOptions}) => {
    form.value.type = selectedOptions[0]?.value
    showTypePicker.value = false
  }

  //确认选择紧急程度
  const onConfirmUrgency = ({selectedOptions}) => {
    form.value.urgency = selectedOptions[0]?.value
    showUrgencyPicker.value = false
  }

  //提交求助
  const onSubmit = () => {
    if (!form.value.content) {
      showToast('请填写求助内容')
      return
    }
    if (form.value.content.length < 5) {
      showToast('求助内容至少5个字')
      return
    }
    helpApi.add({
      elderId: currentElder.value.id,
      type: form.value.type,
      urgency: form.value.urgency,
      content: form.value.content
    }).then(result => {
      if (result.code === 1) {
        showSuccessToast(result.msg)
        //提交成功后返回求助列表页
        router.replace('/help')
      } else {
        showToast(result.msg)
      }
    })
  }
</script>

<template>
  <div class="help-submit">
    <van-nav-bar title="发起求助" left-arrow :fixed="true" placeholder @click-left="router.back()"/>

    <!-- 当前求助老人提示（家属代发起时显示老人姓名） -->
    <div class="elder-tip">
      <van-icon name="user-o"/>
      <span>为 {{ currentElder.realName }} 发起求助</span>
    </div>

    <!-- 求助表单 -->
    <van-form>
      <van-cell-group inset>
        <van-cell title="求助类型" :value="getLabel(typeOptions, form.type)" is-link @click="showTypePicker = true"/>
        <van-cell title="紧急程度" :value="getLabel(urgencyOptions, form.urgency)" is-link @click="showUrgencyPicker = true"/>
      </van-cell-group>

      <!-- 求助内容 -->
      <van-cell-group inset class="content-group">
        <van-field
            v-model="form.content"
            type="textarea"
            label="求助内容"
            placeholder="请简要描述您遇到的困难或需要的帮助"
            :rows="5"
            autosize
            maxlength="500"
            show-word-limit
            :rules="[{required: true, message: '请填写求助内容'}]"
        />
      </van-cell-group>
    </van-form>

    <!-- 提交按钮 -->
    <div class="submit-bar">
      <van-button round block type="primary" @click="onSubmit">提交求助</van-button>
    </div>

    <!-- 求助类型选择弹窗 -->
    <van-popup v-model:show="showTypePicker" position="bottom" round>
      <van-picker
          title="选择求助类型"
          :columns="typeOptions"
          @confirm="onConfirmType"
          @cancel="showTypePicker = false"
      />
    </van-popup>

    <!-- 紧急程度选择弹窗 -->
    <van-popup v-model:show="showUrgencyPicker" position="bottom" round>
      <van-picker
          title="选择紧急程度"
          :columns="urgencyOptions"
          @confirm="onConfirmUrgency"
          @cancel="showUrgencyPicker = false"
      />
    </van-popup>
  </div>
</template>

<style scoped>
  .help-submit {
    min-height: 100%;
    display: flex;
    flex-direction: column;
    padding: 12px 0 20px;
  }

  /* 当前求助老人提示 */
  .elder-tip {
    margin: 0 16px 12px;
    background-color: #E8F3FF;
    border-radius: 8px;
    padding: 10px 12px;
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #1989FA;
  }

  .elder-tip :deep(.van-icon) {
    margin-right: 6px;
    font-size: 15px;
  }

  /* 求助内容卡片 */
  .content-group {
    margin-top: 12px;
  }

  /* 底部提交按钮（内容不满一屏时也固定在页面底部，沿用套餐详情页做法不脱离文档流） */
  .submit-bar {
    margin: auto 16px 0;
    padding-top: 20px;
  }
</style>
