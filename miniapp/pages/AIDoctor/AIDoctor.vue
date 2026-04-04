<!-- src/pages/ai/AIDoctor.vue -->
<template>
  <view class="ai-chat-container">
    <!-- 顶部导航 -->
    <view class="chat-header">
      <view class="header-content">
        <view class="header-title">
          <view class="header-icon">👨‍⚕️</view>
          <view class="header-text">
            <text class="title">AI兽医助手</text>
            <text class="subtitle">智能诊断 · 在线问诊</text>
          </view>
        </view>
        <view class="header-actions">
          <view class="action-btn" @click="clearChat">
            <text class="action-icon">🗑️</text>
          </view>
          <view class="action-btn" @click="newChat">
            <text class="action-icon">➕</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 会话历史 -->
    <view v-if="!showChatHistory && sessions.length > 0" class="history-prompt" @click="showChatHistory = true">
      <text class="history-text">📖 点击查看会话历史</text>
    </view>

    <!-- 会话列表 -->
    <scroll-view v-if="showChatHistory" class="sessions-list" scroll-y>
      <view class="sessions-header">
        <text class="sessions-title">会话历史</text>
        <view class="close-btn" @click="showChatHistory = false">
          <text class="close-icon">✕</text>
        </view>
      </view>

      <view class="session-item" v-for="item in sessions" :key="item.sessionId" @click="switchSession(item.sessionId)">
        <view class="session-icon">
          <text v-if="item.sessionId === currentSessionId">💬</text>
          <text v-else>📄</text>
        </view>
        <view class="session-info">
          <text class="session-name">会话 {{ formatDate(item.createTime) }}</text>
          <text class="session-time">{{ formatTime(item.createTime) }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 聊天消息区 -->
    <scroll-view
      class="chat-messages"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-with-animation="true"
    >
      <!-- 欢迎消息 -->
      <view v-if="messages.length === 0" class="welcome-message">
        <view class="welcome-icon">👨‍⚕️</view>
        <view class="welcome-content">
          <text class="welcome-title">你好！我是AI兽医助手</text>
          <text class="welcome-desc">我可以帮你诊断猪只疾病，提供用药建议和养殖指导。</text>
          <view class="welcome-tips">
            <view class="tip-item">💡 描述猪只的症状</view>
            <view class="tip-item">💡 询问用药剂量</view>
            <view class="tip-item">💡 了解疾病预防</view>
          </view>
        </view>
      </view>

      <!-- 消息列表 -->
      <view v-for="(msg, i) in messages" :key="i" :class="['message-item', msg.role]">
        <view class="message-avatar" v-if="msg.role === 'ai'">
          <text class="avatar-icon">👨‍⚕️</text>
        </view>
        <view class="message-content">
          <view class="message-bubble">
            <text class="message-text">{{ msg.content }}</text>
          </view>
          <text class="message-time">{{ msg.time }}</text>
        </view>
        <view class="message-avatar" v-if="msg.role === 'user'">
          <text class="avatar-icon">👤</text>
        </view>
      </view>

      <!-- AI 加载中 -->
      <view v-if="loadingAI" class="ai-loading">
        <view class="loading-avatar">
          <text class="avatar-icon">👨‍⚕️</text>
        </view>
        <view class="loading-bubble">
          <view class="loading-dots">
            <view class="dot"></view>
            <view class="dot"></view>
            <view class="dot"></view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 输入框区域 -->
    <view class="chat-input">
      <view class="input-wrapper">
        <input
          v-model="inputText"
          class="message-input"
          placeholder="请描述猪只症状..."
          confirm-type="send"
          @confirm="sendMessage"
        />
        <view class="input-actions">
          <view class="action-btn" @click="showChatHistory = true">
            <text class="action-icon">📁</text>
          </view>
          <button class="send-button" :class="{ active: inputText.trim() }" @click="sendMessage">
            <text class="send-icon">{{ loadingAI ? '···' : '➤' }}</text>
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import {
  conversationPage,
  conversationAdd,
  getOllama,
  listBySession
} from '@/api/articles.js'

const userId = uni.getStorageSync('userId')
const sessions = ref([])
const currentSessionId = ref(null)
const messages = ref([])
const inputText = ref('')
const scrollTop = ref(0)
const loadingAI = ref(false)
const showChatHistory = ref(false)

// 自动滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    scrollTop.value = messages.value.length * 9999
  })
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化时间
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 初始化
onMounted(async () => {
  await loadSessions()
})

// 加载会话列表
async function loadSessions() {
  try {
    const params = { pageNum: 1, pageSize: 10, sortField: 'createTime', sortOrder: 'desc' }
    const res = await conversationPage(params)
    sessions.value = res.data?.data || []

    if (sessions.value.length > 0) {
      currentSessionId.value = sessions.value[0].sessionId
      await loadHistory(currentSessionId.value)
    } else {
      await createSession()
    }
  } catch (err) {
    uni.showToast({ title: '加载会话失败', icon: 'none' })
  }
}

// 切换会话
async function switchSession(id) {
  if (loadingAI.value) return
  currentSessionId.value = id
  showChatHistory.value = false
  await loadHistory(id)
}

// 创建新会话
async function createSession() {
  try {
    const res = await conversationAdd({
      userId,
      sessionId: 0,
      userInput: '',
      aiResponse: '',
      conversationTime: '',
      modelName: '',
      responseTime: 0
    })
    if (res.code === 200 && res.data === true) {
      await loadSessions()
    }
  } catch {
    uni.showToast({ title: '创建会话失败', icon: 'none' })
  }
}

// 新建对话
async function newChat() {
  if (loadingAI.value) return
  await createSession()
}

// 清空对话
function clearChat() {
  if (loadingAI.value) return
  uni.showModal({
    title: '提示',
    content: '确定要清空当前对话吗？',
    success: (res) => {
      if (res.confirm) {
        messages.value = []
      }
    }
  })
}

// 加载会话历史记录
async function loadHistory(sessionId) {
  try {
    const res = await listBySession({ sessionId })
    const list = res.data || []
	
	//展平，一条记录-变成两条记录
	messages.value = list.flatMap(item => [
		//用户消息
		{
			role:'user',
			content:item.userInput,
			time:item.conversationTime  ? item.conversationTime.slice(11,19) : ''
		},
		//ai消息
		{
			role:'ai',
			content:item.aiResponse,
			time:item.conversationTime ? item.conversationTime.slice(11,19) :''
		}
	])
	

	
	
    scrollToBottom()
  } catch {
    uni.showToast({ title: '加载历史失败', icon: 'none' })
  }
}

// 发送消息
async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || !currentSessionId.value) return

  // 添加用户消息
  const now = new Date()
  const timeStr = now.toTimeString().slice(0, 8)
  messages.value.push({ role: 'user', content: text, time: timeStr })
  inputText.value = ''
  scrollToBottom()

  loadingAI.value = true

  try {
    const res = await getOllama({
      prompt: text,
      sessionId: currentSessionId.value
    })

    if (res.code === 200 && res.data) {
      messages.value.push({
        role: 'ai',
        content: res.data.aiResponse || 'AI暂时无法回复，请稍后再试～',
        time: res.data.conversationTime
          ? res.data.conversationTime.slice(11, 19)
          : new Date().toTimeString().slice(0, 8)
      })
    } else {
      messages.value.push({
        role: 'ai',
        content: 'AI响应异常，请稍后重试。',
        time: new Date().toTimeString().slice(0, 8)
      })
    }
  } catch (err) {
    uni.showToast({ title: 'AI响应失败', icon: 'none' })
  } finally {
    loadingAI.value = false
    scrollToBottom()
  }
}

</script>

<style lang="scss" scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8f9fa;
}

/* 顶部导航 */
.chat-header {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  padding: 30rpx 30rpx 20rpx;
  position: relative;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  border-bottom: 1px solid #e8e8e8;

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-title {
      display: flex;
      align-items: center;
      flex: 1;

      .header-icon {
        font-size: 52rpx;
        margin-right: 12rpx;
        line-height: 1;
      }

      .header-text {
        display: flex;
        flex-direction: column;

        .title {
          color: #333;
          font-size: 34rpx;
          font-weight: 700;
          margin-bottom: 2rpx;
        }

        .subtitle {
          color: #666;
          font-size: 20rpx;
          font-weight: 400;
        }
      }
    }

    .header-actions {
      display: flex;
      gap: 18rpx;

      .action-btn {
        width: 48rpx;
        height: 48rpx;
        background: #f0f0f0;
        border-radius: 12rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s;

        &:active {
          transform: scale(0.95);
          background: #e8e8e8;
        }

        .action-icon {
          font-size: 28rpx;
          line-height: 1;
        }
      }
    }
  }
}

/* 会话历史提示 */
.history-prompt {
  padding: 20rpx 30rpx;
  background-color: #fff;
  text-align: center;

  .history-text {
    font-size: 26rpx;
    color: #666;
    opacity: 0.7;
  }
}

/* 会话列表 */
.sessions-list {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  z-index: 1000;
  padding: 120rpx 30rpx 30rpx;

  .sessions-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    padding-bottom: 20rpx;
    border-bottom: 1px solid #eee;

    .sessions-title {
      font-size: 34rpx;
      font-weight: 700;
      color: #333;
    }

    .close-btn {
      width: 48rpx;
      height: 48rpx;
      border-radius: 50%;
      background-color: #f5f5f5;
      display: flex;
      align-items: center;
      justify-content: center;

      .close-icon {
        font-size: 32rpx;
        line-height: 1;
      }
    }
  }

  .session-item {
    display: flex;
    align-items: center;
    padding: 20rpx;
    background-color: #f9f9f9;
    border-radius: 12rpx;
    margin-bottom: 12rpx;
    transition: all 0.2s;

    &:active {
      transform: scale(0.99);
      background-color: #f0f0f0;
    }

    .session-icon {
      width: 40rpx;
      height: 40rpx;
      border-radius: 50%;
      background-color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16rpx;
      font-size: 24rpx;
      line-height: 1;
      box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.06);
    }

    .session-info {
      flex: 1;

      .session-name {
        display: block;
        font-size: 26rpx;
        color: #333;
        margin-bottom: 4rpx;
        font-weight: 500;
      }

      .session-time {
        display: block;
        font-size: 20rpx;
        color: #999;
      }
    }
  }
}

/* 聊天消息区 */
.chat-messages {
  flex: 1;
  padding: 20rpx 30rpx 10rpx;
  overflow-y: auto;
  background-color: #f8f9fa;
}

/* 欢迎消息 */
.welcome-message {
  display: flex;
  align-items: center;
  padding: 36rpx;
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  border: 1px solid #e8e8e8;

  .welcome-icon {
    font-size: 64rpx;
    margin-right: 20rpx;
    line-height: 1;
  }

  .welcome-content {
    flex: 1;

    .welcome-title {
      display: block;
      font-size: 30rpx;
      font-weight: 700;
      color: #333;
      margin-bottom: 10rpx;
    }

    .welcome-desc {
      display: block;
      font-size: 24rpx;
      color: #666;
      margin-bottom: 20rpx;
      line-height: 1.5;
    }

    .welcome-tips {
      .tip-item {
        display: flex;
        align-items: center;
        padding: 10rpx 0;
        font-size: 22rpx;
        color: #555;
        font-weight: 500;

        &::before {
          content: '';
          display: inline-block;
          width: 6rpx;
          height: 6rpx;
          background-color: #999;
          border-radius: 50%;
          margin-right: 10rpx;
        }
      }
    }
  }
}

/* 消息项 */
.message-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20rpx;

  &.ai {
    justify-content: flex-start;

    .message-avatar {
      margin-right: 14rpx;
    }

    .message-content {
      max-width: 80%;

      .message-bubble {
        background-color: #fff;
        border: 1px solid #e8e8e8;
        color: #333;
        border-radius: 16rpx 16rpx 16rpx 4rpx;
      }
    }
  }

  &.user {
    justify-content: flex-end;

    .message-avatar {
      margin-left: 14rpx;
    }

    .message-content {
      align-items: flex-end;
      max-width: 80%;

      .message-bubble {
        background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
        color: #333;
        border-radius: 16rpx 16rpx 4rpx 16rpx;
      }
    }
  }

  .message-avatar {
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    background-color: #fff;
    box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.08);

    .avatar-icon {
      font-size: 36rpx;
      line-height: 1;
    }
  }

  .message-content {
    display: flex;
    flex-direction: column;

    .message-bubble {
      padding: 20rpx;
      max-width: 100%;
      box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.06);
      word-break: break-word;
      white-space: pre-wrap;
      line-height: 1.6;
      font-size: 26rpx;

      .message-text {
        display: block;
      }
    }

    .message-time {
      font-size: 18rpx;
      color: #999;
      margin-top: 6rpx;
      align-self: flex-end;
    }
  }
}

/* AI 加载中 */
.ai-loading {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;

  .loading-avatar {
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 14rpx;
    background-color: #fff;
    box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.08);

    .avatar-icon {
      font-size: 36rpx;
      line-height: 1;
    }
  }

  .loading-bubble {
    background-color: #fff;
    border: 1px solid #e8e8e8;
    border-radius: 16rpx 16rpx 16rpx 4rpx;
    padding: 20rpx;
    box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.06);

    .loading-dots {
      display: flex;
      gap: 6rpx;

      .dot {
        width: 10rpx;
        height: 10rpx;
        background-color: #999;
        border-radius: 50%;
        animation: dot-bounce 1.4s infinite ease-in-out;

        &:nth-child(1) {
          animation-delay: -0.32s;
        }

        &:nth-child(2) {
          animation-delay: -0.16s;
        }
      }
    }
  }
}

@keyframes dot-bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 输入框区域 */
.chat-input {
  padding: 20rpx 30rpx 30rpx;
  background-color: #fff;
  border-top: 1px solid #e8e8e8;

  .input-wrapper {
    display: flex;
    align-items: center;
    background-color: #f8f9fa;
    border-radius: 48rpx;
    padding: 6rpx 12rpx;
    border: 1px solid #e8e8e8;

    .message-input {
      flex: 1;
      padding: 16rpx 20rpx;
      font-size: 26rpx;
      background: transparent;
      border: none;
      outline: none;
      color: #333;
    }

    .input-actions {
      display: flex;
      align-items: center;
      gap: 12rpx;

      .action-btn {
        width: 48rpx;
        height: 48rpx;
        border-radius: 50%;
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.08);

        .action-icon {
          font-size: 28rpx;
          line-height: 1;
        }
      }

      .send-button {
        width: 48rpx;
        height: 48rpx;
        border-radius: 50%;
        background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
        transition: all 0.2s;
        border: 1px solid #a5d6a7;

        &.active {
          transform: scale(1.05);
          box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
        }

        &:active {
          transform: scale(0.95);
        }

        .send-icon {
          font-size: 32rpx;
          color: #333;
          line-height: 1;
        }
      }
    }
  }
}
</style>
