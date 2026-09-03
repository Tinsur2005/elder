<script setup>
  import {computed, nextTick, ref} from 'vue'
  import {showConfirmDialog, showToast} from 'vant'
  import {useRouter} from 'vue-router'
  import chatApi from '@/api/chat.js'
  import {useUserInfoStore} from '@/store/userInfo.js'

  const router = useRouter()
  const userInfoStore = useUserInfoStore()

  // ================== 对象 ==================

  // 欢迎语（仅无聊天记录时展示，不存入历史记录）
  const WELCOME = '您好，我是您的智能助手小邻，很高兴为您服务。\n您可以和我聊天解闷，也可以问我一些健康方面的小问题。'

  // 聊天记录列表（role: user-用户发送 / ai-小邻回复）
  const messageList = ref([])

  // ================== 变量 ==================

  //输入框内容
  const inputText = ref('')
  //是否正在接收AI回复（回复期间禁止再次发送）
  const generating = ref(false)
  //消息列表容器（用于自动滚动到底部）
  const messageAreaRef = ref(null)

  // 聊天记录本地缓存key（按登录用户id区分，避免换账号串记录）
  const storageKey = computed(() => `aiChatHistory_${userInfoStore.user.id}`)

  // ================== 下拉数据 ==================

  // 加载本地保存的聊天记录
  const loadHistory = () => {
    try {
      messageList.value = JSON.parse(localStorage.getItem(storageKey.value)) || []
    } catch (e) {
      // 缓存内容异常时按无记录处理
      messageList.value = []
    }
  }
  loadHistory()

  // ================== 方法 ==================

  //保存聊天记录到本地
  const saveHistory = () => {
    localStorage.setItem(storageKey.value, JSON.stringify(messageList.value))
  }

  //清空聊天记录
  const clearHistory = () => {
    showConfirmDialog({
      title: '提示',
      message: '确认清空聊天记录么?',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    }).then(() => {
      messageList.value = []
      localStorage.removeItem(storageKey.value)
    }).catch(() => {
      //取消清空
    })
  }

  //让消息列表滚动到底部
  const scrollToBottom = () => {
    nextTick(() => {
      if (messageAreaRef.value) {
        messageAreaRef.value.scrollTop = messageAreaRef.value.scrollHeight
      }
    })
  }

  //进入页面加载完历史记录后，直接定位到最新一条聊天
  //等DOM和布局都稳定后再滚
  nextTick(() => {
    nextTick(scrollToBottom)
  })

  //发送消息并接收AI流式回复
  const send = () => {
    const message = inputText.value.trim()
    if (!message) {
      showToast('请输入您想咨询的问题')
      return
    }
    if (generating.value) {
      showToast('小邻正在回复，请稍候')
      return
    }
    inputText.value = ''
    generating.value = true
    messageList.value.push({role: 'user', content: message})
    //AI回复占位消息，流式内容往里追加
    const reply = {role: 'ai', content: ''}
    messageList.value.push(reply)
    scrollToBottom()
    chatApi.chatStream(message, content => {
      reply.content += content
      scrollToBottom()
    }).then(() => {
      generating.value = false
      saveHistory()
    }).catch(() => {
      generating.value = false
      //回复失败时移除空的占位消息，有内容的保留
      if (!reply.content) {
        messageList.value.splice(messageList.value.indexOf(reply), 1)
      }
      scrollToBottom()
    })
  }
</script>

<template>
  <div class="ai-chat">
    <van-nav-bar title="AI助手" left-arrow :fixed="true" placeholder @click-left="router.back()">
      <template #right>
        <van-icon name="delete-o" size="20" @click="clearHistory"/>
      </template>
    </van-nav-bar>

    <!-- 消息列表 -->
    <div class="chat-area" ref="messageAreaRef">
      <!-- 欢迎语（无聊天记录时展示） -->
      <div class="message-row" v-if="messageList.length === 0">
        <div class="message-avatar message-avatar-ai">
          <van-icon name="chat-o" size="20"/>
        </div>
        <div class="message-bubble message-bubble-ai">{{ WELCOME }}</div>
      </div>
      <!-- 聊天记录 -->
      <div
          class="message-row"
          :class="{'message-row-user': msg.role === 'user'}"
          v-for="(msg, index) in messageList"
          :key="index"
      >
        <div class="message-avatar" :class="msg.role === 'user' ? 'message-avatar-user' : 'message-avatar-ai'">
          <van-icon :name="msg.role === 'user' ? 'user-o' : 'chat-o'" size="20"/>
        </div>
        <div class="message-bubble" :class="msg.role === 'user' ? 'message-bubble-user' : 'message-bubble-ai'">
          <span v-if="msg.content">{{ msg.content }}</span>
          <!-- AI回复尚未收到内容时展示加载中 -->
          <van-loading v-else-if="generating" size="16" vertical>正在思考</van-loading>
        </div>
      </div>
    </div>

    <!-- 底部输入栏 -->
    <div class="chat-input">
      <van-field
          v-model="inputText"
          type="textarea"
          rows="1"
          autosize
          maxlength="500"
          placeholder="请输入您想咨询的问题"
      />
      <van-button type="primary" round :disabled="generating" @click="send">发送</van-button>
    </div>
  </div>
</template>

<style scoped>
  /* 页面高度扣除底部导航栏，消息区滚动、输入栏固定在页面底部 */
  .ai-chat {
    height: calc(100vh - 50px - constant(safe-area-inset-bottom));
    height: calc(100vh - 50px - env(safe-area-inset-bottom));
    display: flex;
    flex-direction: column;
    background-color: #F5F6FA;
  }

  /* 消息列表（flex:1 占满剩余空间并内部滚动） */
  .chat-area {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    padding: 12px;
  }

  .message-row {
    display: flex;
    align-items: flex-start;
    margin-bottom: 14px;
  }

  /* 用户消息靠右排布 */
  .message-row-user {
    flex-direction: row-reverse;
  }

  .message-avatar {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .message-avatar-ai {
    background-color: #E8F3FF;
    color: #1989FA;
  }

  .message-avatar-user {
    background-color: #1989FA;
    color: #FFFFFF;
  }

  .message-bubble {
    max-width: calc(100% - 100px);
    margin: 0 10px;
    padding: 10px 14px;
    border-radius: 12px;
    font-size: 15px;
    line-height: 24px;
    white-space: pre-wrap;
    word-break: break-all;
  }

  .message-bubble-ai {
    background-color: #FFFFFF;
    color: #323233;
  }

  .message-bubble-user {
    background-color: #1989FA;
    color: #FFFFFF;
  }

  /* 底部输入栏 */
  .chat-input {
    display: flex;
    align-items: flex-end;
    gap: 10px;
    padding: 8px 12px;
    padding-bottom: calc(8px + constant(safe-area-inset-bottom));
    padding-bottom: calc(8px + env(safe-area-inset-bottom));
    background-color: #FFFFFF;
  }

  .chat-input :deep(.van-field) {
    background-color: #F5F6FA;
    border-radius: 20px;
    padding: 8px 14px;
  }

  .chat-input .van-button {
    flex-shrink: 0;
    padding: 0 20px;
    height: 36px;
  }
</style>
