<template>
  <el-container class="layout-container">
    <!-- 左侧导航 -->
    <el-aside class="layout-aside" :width="isCollapsed ? '64px' : '240px'">
      <div class="logo" :class="{ 'collapsed': isCollapsed }">
        <div class="logo-icon">
          <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
            <path d="M14 26C20.6274 26 26 20.6274 26 14C26 7.37258 20.6274 2 14 2C7.37258 2 2 7.37258 2 14C2 20.6274 7.37258 26 14 26Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M18 14C18 16.2091 16.2091 18 14 18C11.7909 18 10 16.2091 10 14C10 11.7909 11.7909 10 14 10C16.2091 10 18 11.7909 18 14Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 9L12 12" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 12L19 9" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <transition name="fade">
          <span v-if="!isCollapsed" class="logo-text">健康管理</span>
        </transition>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        class="el-menu-vertical-demo"
        @select="handleSelect"
      >
        <el-menu-item index="/UserMgt">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/newsArticlesMgt">
          <el-icon><Document /></el-icon>
          <span>新闻信息</span>
        </el-menu-item>
        <el-menu-item index="/articleTypeMgt">
          <el-icon><Folder /></el-icon>
          <span>文章类型</span>
        </el-menu-item>
        <el-menu-item index="/ArticleMgt">
          <el-icon><Edit /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/filesMgt">
          <el-icon><FolderOpened /></el-icon>
          <span>文件信息</span>
        </el-menu-item>
        <el-menu-item index="/pageviewMgt">
          <el-icon><View /></el-icon>
          <span>浏览统计</span>
        </el-menu-item>
        <el-menu-item index="/illnessKindMgt">
          <el-icon><FirstAidKit /></el-icon>
          <span>疾病分类</span>
        </el-menu-item>
        <el-menu-item index="/illnessMgt">
          <el-icon><Memo /></el-icon>
          <span>疾病管理</span>
        </el-menu-item>
        <el-menu-item index="/illnessMedicineMgt">
          <el-icon><Document /></el-icon>
          <span>疾病药品</span>
        </el-menu-item>
        <el-menu-item index="/medicineMgt">
          <el-icon><Document /></el-icon>
          <span>药品管理</span>
        </el-menu-item>
        <el-menu-item index="/FeedbackMgt">
          <el-icon><Message /></el-icon>
          <span>用户反馈</span>
        </el-menu-item>
        <el-menu-item index="/CoverSationMgt">
          <el-icon><ChatDotSquare /></el-icon>
          <span>对话记录</span>
        </el-menu-item>
        <el-menu-item index="/HistoryMgt">
          <el-icon><Clock /></el-icon>
          <span>搜索日志</span>
        </el-menu-item>
        <el-menu-item index="/KnowledgeMgt">
          <el-icon><Document /></el-icon>
          <span>知识库</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主体内容区 -->
    <el-container class="layout-content">
      <!-- 顶部导航栏 -->
      <el-header class="layout-header">
        <div class="header-left" @click="toggleCollapse">
          <el-icon class="collapse-icon" :size="18">
            <Fold v-if="isCollapsed" />
            <Expand v-else />
          </el-icon>
          <transition name="fade">
            <div v-if="!isCollapsed" class="header-title">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item>管理中心</el-breadcrumb-item>
                <el-breadcrumb-item>{{ activeMenuLabel }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
          </transition>
        </div>

        <div class="header-right">
          <el-badge :value="0" :hidden="true" class="header-badge">
            <el-icon class="bell-icon"><Bell /></el-icon>
          </el-badge>

          <div class="user-info">
            <div class="user-avatar-wrapper">
              <img
                :src="userInfo.imgPath || '/default-avatar.png'"
                alt="用户头像"
                class="user-avatar"
                loading="lazy"
              />
            </div>
            <transition name="fade">
              <span v-if="!isCollapsed" class="user-name">{{ userInfo.userName }}</span>
            </transition>
          </div>
        </div>
      </el-header>

      <el-main class="layout-main">
        <div class="content-wrapper">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  User,
  Document,
  Folder,
  Edit,
  FolderOpened,
  View,
  FirstAidKit,
  Memo,
  Message,
  ChatDotSquare,
  Clock,
  Fold,
  Expand,
  Bell
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const activeMenu = ref(route.path)
const activeMenuLabel = ref('')
const isCollapsed = ref(false)

// ✅ 从本地获取用户信息
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const menuItems = [
  { path: '/UserMgt', label: '用户管理' },
  { path: '/newsArticlesMgt', label: '新闻信息' },
  { path: '/articleTypeMgt', label: '文章类型' },
  { path: '/ArticleMgt', label: '文章管理' },
  { path: '/filesMgt', label: '文件信息' },
  { path: '/pageviewMgt', label: '浏览统计' },
  { path: '/illnessKindMgt', label: '疾病分类' },
  { path: '/illnessMgt', label: '疾病管理' },
  { path: '/illnessMedicineMgt', label: '疾病药品' },
  { path: '/medicineMgt', label: '药品管理' },
  { path: '/FeedbackMgt', label: '用户反馈' },
  { path: '/CoverSationMgt', label: '对话记录' },
  { path: '/HistoryMgt', label: '搜索日志' },
  { path: '/KnowledgeMgt', label: '知识库' }
]

watch(
  () => route.path,
  (newPath) => {
    activeMenu.value = newPath
    const item = menuItems.find(item => item.path === newPath)
    activeMenuLabel.value = item ? item.label : ''
  },
  { immediate: true }
)

const handleSelect = (index) => {
  router.push(index)
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
/* 现代简约布局容器 */
.layout-container {
  display: flex;
  height: 100vh;
  background-color: #f8f9fa;
  transition: all 0.3s ease;
}

/* 左侧导航栏 */
.layout-aside {
  background: linear-gradient(180deg, #5a6c7d 0%, #41505d 100%);
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
  overflow: hidden;
}

.layout-aside:hover {
  width: 240px;
}

/* Logo区域 */
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.logo.collapsed {
  justify-content: center;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  transition: margin-right 0.3s ease;
}

.logo.collapsed .logo-icon {
  margin-right: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

/* 菜单样式 */
.el-menu-vertical-demo {
  background: transparent;
  border-right: none;
  overflow-y: auto;
  height: calc(100% - 64px);
}

.el-menu-vertical-demo::-webkit-scrollbar {
  width: 4px;
}

.el-menu-vertical-demo::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
}

.el-menu-item {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  padding: 0 20px;
  margin: 4px 0;
  border-radius: 8px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  transform: translateX(4px);
}

.el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
  box-shadow: inset 4px 0 8px rgba(0, 0, 0, 0.15);
}

.el-menu-item i {
  margin-right: 12px;
  font-size: 18px;
}

.el-menu--collapse .el-menu-item i {
  margin-right: 0;
}

.el-menu--collapse .el-menu-item span {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.el-menu--collapse .el-sub-menu__title i {
  margin-right: 0;
}

/* 内容区域 */
.layout-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* 顶部导航栏 */
.layout-header {
  background: #fff;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.collapse-icon {
  color: #6b7280;
  margin-right: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.header-title {
  display: flex;
  align-items: center;
}

/* 用户信息 */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-badge .bell-icon {
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.header-badge .bell-icon:hover {
  color: #5a6c7d;
  transform: rotate(15deg);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: default;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.user-info:hover {
  background: #f3f4f6;
}

.user-avatar-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.user-avatar-wrapper:hover {
  box-shadow: 0 4px 12px rgba(90, 108, 125, 0.25);
  transform: scale(1.05);
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-size: 15px;
  font-weight: 500;
  color: #374151;
}

/* 主内容区 */
.layout-main {
  flex: 1;
  padding: 20px;
  overflow: hidden;
}

.content-wrapper {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  height: 100%;
  overflow: auto;
}

.content-wrapper::-webkit-scrollbar {
  width: 6px;
}

.content-wrapper::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 3px;
}

.content-wrapper::-webkit-scrollbar-thumb:hover {
  background: #d1d5db;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
