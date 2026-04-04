<template>
  <div class="doctor-container">
    <nav2 />

    <div class="chat-wrapper">
      <!-- 左侧会话列表 -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <div class="brand-icon">
            <svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
              <circle cx="50" cy="45" r="25" fill="none" stroke="#10b981" stroke-width="4"/>
              <circle cx="42" cy="40" r="4" fill="#10b981"/>
              <circle cx="58" cy="40" r="4" fill="#10b981"/>
              <path d="M40 50 Q50 55 60 50" stroke="#10b981" stroke-width="3" fill="none" stroke-linecap="round"/>
            </svg>
          </div>
          <span class="brand-name">AI 兽医助手</span>
        </div>

        <div class="conversation-list">
          <div
            v-for="(conv, index) in conversations"
            :key="index"
            class="conversation-item"
            :class="{ active: activeConv === index }"
            @click="selectConversation(index)"
          >
            <span class="conv-icon">💬</span>
            <div class="conv-info">
              <h4>{{ conv.petName || '新对话' }}</h4>
              <p class="conv-preview">{{ getLastMessage(conv.messages) }}</p>
            </div>
          </div>
        </div>

        <button class="new-chat-btn" @click="handleNewChat">
          <svg viewBox="0 0 24 24" width="16" height="16">
            <path d="M12 4v16M4 12h16" stroke="white" stroke-width="2" fill="none" stroke-linecap="round"/>
          </svg>
          <span>新建对话</span>
        </button>
      </aside>

      <!-- 右侧聊天区域 -->
      <div class="chat-section">
        <!-- 顶部状态栏 -->
        <div class="chat-header">
          <div class="header-left">
            <div class="ai-icon">
              <svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
                <circle cx="50" cy="45" r="25" fill="none" stroke="#10b981" stroke-width="4"/>
                <circle cx="42" cy="40" r="4" fill="#10b981"/>
                <circle cx="58" cy="40" r="4" fill="#10b981"/>
                <path d="M40 50 Q50 55 60 50" stroke="#10b981" stroke-width="3" fill="none" stroke-linecap="round"/>
              </svg>
            </div>
            <div class="ai-info">
              <h2>智能兽医 Dr.Pig</h2>
              <p class="status-text">● 在线 · 24小时专业服务</p>
            </div>
          </div>
        </div>

        <!-- 消息区域 -->
        <div class="messages-container" ref="messagesContainer">
          <div v-if="currentMessages.length === 0" class="welcome-message">
            <div class="welcome-icon">
              <svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
                <circle cx="50" cy="50" r="40" fill="none" stroke="#e5e7eb" stroke-width="3"/>
                <path d="M35 55 Q50 60 65 55" stroke="#e5e7eb" stroke-width="3" fill="none" stroke-linecap="round"/>
                <circle cx="40" cy="40" r="4" fill="#e5e7eb"/>
                <circle cx="60" cy="40" r="4" fill="#e5e7eb"/>
              </svg>
            </div>
            <h3>您好！我是智能兽医 Dr.Pig</h3>
            <p>我可以帮您解答关于猪只健康的问题</p>
            <div class="suggestion-buttons">
              <button class="suggestion-btn" @click="quickAsk('猪只出现食欲不振怎么办？')">猪只食欲不振</button>
              <button class="suggestion-btn" @click="quickAsk('如何预防猪瘟？')">预防猪瘟</button>
              <button class="suggestion-btn" @click="quickAsk('猪只腹泻应该怎么治疗？')">猪只腹泻治疗</button>
            </div>
          </div>

          <div v-for="(msg, index) in currentMessages" :key="index" class="message-item" :class="msg.sender">
            <div class="message-avatar">
              <img v-if="msg.sender === 'user'" :src="userInfo.avatar" alt="用户头像" />
              <div v-else class="ai-avatar">
                <svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="50" cy="45" r="25" fill="none" stroke="#10b981" stroke-width="4"/>
                  <circle cx="42" cy="40" r="4" fill="#10b981"/>
                  <circle cx="58" cy="40" r="4" fill="#10b981"/>
                  <path d="M40 50 Q50 55 60 50" stroke="#10b981" stroke-width="3" fill="none" stroke-linecap="round"/>
                </svg>
              </div>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="msg.content"></div>
              <span v-if="msg.timestamp" class="message-time">{{ msg.timestamp }}</span>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="isLoading" class="loading-message">
            <div class="loading-bubbles">
              <div class="bubble"></div>
              <div class="bubble"></div>
              <div class="bubble"></div>
            </div>
            <span class="loading-text">Dr.Pig 正在思考...</span>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-section">
          <div class="input-wrapper">
            <textarea
              v-model="inputMessage"
              placeholder="描述您的问题，例如：猪只出现食欲不振、精神萎靡等症状..."
              @keydown.enter="handleSend"
              class="message-input"
            ></textarea>
            <button
              @click="handleSend"
              class="send-btn"
              :disabled="!inputMessage.trim() || isLoading"
            >
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M2 12L22 2L12 12L22 22L2 12Z" fill="#ffffff" stroke="#ffffff" stroke-width="1"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import nav2 from '@/components/nav2.vue'
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { getOllama, getHistoryNum, sessionList, sessionCreate, listBySession } from '@/api/admin/conversation'
import DOMPurify from 'dompurify'

// 用户信息
const userInfo = reactive({
  name: '用户',
  avatar: new URL('@/assets/images/icons/avatar.jpg', import.meta.url).href
})

// 状态管理
const conversations = ref([])
const currentSessionId = ref(null)
const activeConv = ref(0)
const inputMessage = ref('')
const messagesContainer = ref(null)
const isLoading = ref(false)

// 计算属性
const currentMessages = computed(() => {
  return conversations.value[activeConv.value]?.messages || []
})

// 渲染 Markdown
const renderMarkdown = (md = '') => {
  if (!md) return ''
  let html = md

  // 代码块
  html = html.replace(/```([\s\S]*?)```/g, '<pre class="code-block">$1</pre>')
  // 行内代码
  html = html.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>')
  // 加粗
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
  // 链接
  html = html.replace(/\[([^\]]+)\]\((https?:[^\s)]+)\)/g, '<a href="$2" target="_blank" class="link">$1</a>')

  return DOMPurify.sanitize(html)
}

// 获取最后一条消息预览
const getLastMessage = (messages) => {
  if (!messages || messages.length === 0) return '暂无消息'
  const lastMsg = messages[messages.length - 1]
  const text = lastMsg.content.replace(/<[^>]*>/g, '').replace(/\s+/g, ' ').trim()
  return text.length > 20 ? text.slice(0, 20) + '...' : text
}

// 选择会话
const selectConversation = async (index) => {
  if (activeConv.value === index) return

  activeConv.value = index
  const sessionId = conversations.value[index]?.sessionId

  if (sessionId) {
    currentSessionId.value = sessionId
    try {
      const h = await listBySession(sessionId)
      if (h?.code === 200 && Array.isArray(h.data)) {
        conversations.value[index].messages = h.data.flatMap(m => {
          const arr = []
          if (m.userInput) arr.push({
            sender: 'user',
            content: DOMPurify.sanitize(m.userInput),
            timestamp: ''
          })
          if (m.aiResponse) arr.push({
            sender: 'ai',
            content: renderMarkdown(m.aiResponse),
            timestamp: ''
          })
          return arr
        })
      }
    } catch (e) {
      console.error('加载会话消息失败:', e)
    }
  }

  nextTick(() => {
    scrollToBottom()
  })
}

// 发送消息
const handleSend = async () => {
  const content = inputMessage.value.trim()
  if (!content || isLoading.value) return

  // 添加用户消息
  currentMessages.value.push({
    sender: 'user',
    content: DOMPurify.sanitize(content),
    timestamp: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  })

  inputMessage.value = ''
  scrollToBottom()

  // 显示加载状态
  isLoading.value = true

  try {
    const res = await getOllama(content, currentSessionId.value)
    const reply = res?.code === 200 && res.data
      ? (res.data.aiResponse || '抱歉，我没有获取到有效回复。')
      : '抱歉，服务繁忙，请稍后重试。'

    // 添加AI回复
    currentMessages.value.push({
      sender: 'ai',
      content: renderMarkdown(reply),
      timestamp: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })

    if (res?.data?.sessionId) {
      currentSessionId.value = res.data.sessionId
      // 更新会话标题（如果有）
      if (conversations.value[activeConv.value]) {
        conversations.value[activeConv.value].sessionId = res.data.sessionId
      }
    }
  } catch (e) {
    currentMessages.value.push({
      sender: 'ai',
      content: '网络异常或服务器错误，请稍后再试。',
      timestamp: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

// 快捷提问
const quickAsk = (question) => {
  inputMessage.value = question
  setTimeout(() => {
    handleSend()
  }, 100)
}

// 新对话
const handleNewChat = async () => {
  try {
    const created = await sessionCreate('新对话', undefined)
    if (created?.code === 200 && created.data) {
      conversations.value.unshift({
        petName: created.data.title || '新对话',
        sessionId: created.data.id,
        messages: []
      })
      activeConv.value = 0
      currentSessionId.value = created.data.id
      // 清空输入框
      inputMessage.value = ''
    }
  } catch (e) {
    console.error('创建会话失败:', e)
    // 如果创建失败，仍然创建一个本地会话
    conversations.value.unshift({
      petName: '新对话',
      sessionId: null,
      messages: []
    })
    activeConv.value = 0
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 初始化
onMounted(async () => {
  try {
    const s = await sessionList()
    if (s?.code === 200 && Array.isArray(s.data)) {
      conversations.value = s.data.map(item => ({
        petName: item.title || '新对话',
        sessionId: item.id,
        messages: []
      }))

      if (conversations.value.length > 0) {
        activeConv.value = 0
        currentSessionId.value = conversations.value[0].sessionId

        const h = await listBySession(currentSessionId.value)
        if (h?.code === 200 && Array.isArray(h.data)) {
          conversations.value[0].messages = h.data.flatMap(m => {
            const arr = []
            if (m.userInput) arr.push({
              sender: 'user',
              content: DOMPurify.sanitize(m.userInput),
              timestamp: ''
            })
            if (m.aiResponse) arr.push({
              sender: 'ai',
              content: renderMarkdown(m.aiResponse),
              timestamp: ''
            })
            return arr
          })
        }
      } else {
        await handleNewChat()
      }
    } else {
      // 没有会话，创建新会话
      await handleNewChat()
    }
  } catch (e) {
    console.error('加载会话失败:', e)
    // 出错时创建新会话
    await handleNewChat()
  }
  scrollToBottom()
})
</script>

<style scoped>
.doctor-container {
  min-height: 100vh;
  background: #f9fafb;
  padding-top: 80px;
  padding-bottom: 20px;
}

.chat-wrapper {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  gap: 24px;
}

/* 左侧会话列表 */
.sidebar {
  width: 320px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
}

.sidebar-header {
  padding: 24px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.brand-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.conversation-item {
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  gap: 12px;
  align-items: center;
  background: white;
}

.conversation-item:hover {
  background: #f9fafb;
}

.conversation-item.active {
  background: #f0fdfa;
  border-left: 3px solid #10b981;
}

.conv-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.conv-info {
  flex: 1;
  min-width: 0;
}

.conv-info h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-preview {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.new-chat-btn {
  margin: 16px;
  padding: 10px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
  width: calc(100% - 32px);
}

.new-chat-btn:hover {
  background: #0da271;
  transform: scale(1.02);
}

/* 右侧聊天区域 */
.chat-section {
  flex: 1;
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 顶部状态栏 */
.chat-header {
  padding: 20px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ai-icon {
  width: 50px;
  height: 50px;
  flex-shrink: 0;
}

.ai-info h2 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.status-text {
  margin: 0;
  font-size: 13px;
  color: #10b981;
}

/* 消息区域 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f9fafb;
}

.welcome-message {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.welcome-icon {
  margin: 0 auto 24px;
  width: 80px;
  height: 80px;
}

.welcome-message h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #1f2937;
  font-weight: 600;
}

.welcome-message p {
  margin: 0 0 24px 0;
  font-size: 14px;
}

.suggestion-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.suggestion-btn {
  padding: 8px 16px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 13px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
}

.suggestion-btn:hover {
  border-color: #10b981;
  background: #f0fdfa;
  color: #10b981;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  animation: fadeIn 0.3s ease;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.ai-avatar {
  width: 40px;
  height: 40px;
}

.message-content {
  max-width: 70%;
  position: relative;
}

.message-item.ai .message-content::before {
  content: '';
  position: absolute;
  left: -8px;
  top: 10px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 8px solid white;
}

.message-item.user .message-content::before {
  content: '';
  position: absolute;
  right: -8px;
  top: 10px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-left: 8px solid #10b981;
}

.message-item.ai .message-text {
  background: white;
  color: #374151;
  border-radius: 12px 12px 12px 2px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.message-item.user .message-text {
  background: #10b981;
  color: white;
  border-radius: 12px 12px 2px 12px;
}

.message-text {
  padding: 12px 16px;
  line-height: 1.6;
  font-size: 14px;
  word-break: break-word;
}

.message-time {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: #9ca3af;
  text-align: right;
}

.loading-message {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.loading-bubbles {
  display: flex;
  gap: 4px;
  padding-left: 8px;
}

.bubble {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  animation: bubbleFloat 1.4s infinite;
}

.bubble:nth-child(2) {
  animation-delay: 0.2s;
}

.bubble:nth-child(3) {
  animation-delay: 0.4s;
}

.loading-text {
  font-size: 13px;
  color: #6b7280;
}

/* 输入区域 */
.input-section {
  padding: 20px;
  border-top: 1px solid #f3f4f6;
  background: white;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  min-height: 48px;
  max-height: 150px;
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  transition: all 0.2s;
  font-family: inherit;
}

.message-input:focus {
  outline: none;
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.send-btn {
  width: 48px;
  height: 48px;
  border: none;
  border-radius: 12px;
  background: #10b981;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:hover:not(:disabled) {
  background: #0da271;
  transform: scale(1.05);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 代码块样式 */
.code-block {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  margin: 8px 0;
  white-space: pre-wrap;
}

.inline-code {
  background: #f9fafb;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.link {
  color: #10b981;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bubbleFloat {
  0%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  50% {
    transform: translateY(-4px);
    opacity: 1;
  }
}

/* 响应式 */
@media (max-width: 1024px) {
  .sidebar {
    width: 280px;
  }
}

@media (max-width: 768px) {
  .chat-wrapper {
    flex-direction: column;
    padding: 0 12px;
  }

  .sidebar {
    width: 100%;
    height: auto;
    max-height: 400px;
  }

  .conversation-list {
    max-height: 300px;
  }

  .messages-container {
    height: auto;
    min-height: 400px;
    padding: 16px;
  }

  .message-content {
    max-width: 85%;
  }

  .suggestion-buttons {
    flex-direction: column;
    align-items: center;
  }

  .suggestion-btn {
    width: 100%;
    max-width: 300px;
  }
}
</style>
