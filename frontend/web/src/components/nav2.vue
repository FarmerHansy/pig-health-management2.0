<template>
  <div class="main-nav" ref="navBarRef">
    <!-- Logo区域 -->
    <div class="logo-box" @click="goHome">
      <div class="logo-icon-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M15.5 9.5C15.5 9.5 14.5 8.5 12 8.5C9.5 8.5 8.5 9.5 8.5 9.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M9.5 12H9.51" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M14.5 12H14.51" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M15.5 14.5C15.5 14.5 15 16 12 16C9 16 8.5 14.5 8.5 14.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="logo-text">猪健康管理</span>
    </div>

    <!-- 导航项 -->
    <ul class="nav-list">
      <li
        v-for="(item, index) in navItems"
        :key="index"
        :class="{ active: activeIndex === index }"
        @click="handleNavClick(index)"
      >
        <router-link :to="navRoutes[index]">{{ item }}</router-link>
      </li>
    </ul>

    <!-- 登录状态 -->
    <div v-if="userStore.isLoggedIn" class="user-section">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="el-dropdown-link">
          <span class="user-name">欢迎你：{{ userStore.userInfo.userName || userStore.userInfo.userAccount }}</span>
          <img
            v-if="userStore.userInfo.imgPath"
            :src="userStore.userInfo.imgPath"
            class="user-avatar"
            alt="User Avatar"
          />
          <i v-else class="fas fa-user-circle default-avatar"></i>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="myInfo">用户中心</el-dropdown-item>
            <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <div v-else class="auth-section">
      <el-button type="success" @click="handleLogin">登录</el-button>
      <router-link to="/register">
        <el-button type="success" plain>注册</el-button>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { logout } from '@/api/admin/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const navItems = ['首页', '资讯中心', '疾病百科', '药品中心', '智能问诊', '养殖文章', '搜索']
const navRoutes = ['/home', '/news', '/findIllness', '/findMedicines', '/doctor', '/article', '/serachBottom']
const activeIndex = ref(0)
const navBarRef = ref(null)

const setActiveIndexByRoute = () => {
  const currentPath = route.path
  const foundIndex = navRoutes.findIndex(r => currentPath.startsWith(r))
  if (foundIndex !== -1) activeIndex.value = foundIndex
}
watch(route, setActiveIndexByRoute)
onMounted(() => {
  setActiveIndexByRoute()
  window.addEventListener('scroll', handleScroll)
})
onUnmounted(() => window.removeEventListener('scroll', handleScroll))

const handleNavClick = (i) => (activeIndex.value = i)

const handleCommand = (cmd) => {
  if (cmd === 'myInfo') router.push('/user')
  if (cmd === 'changePassword') router.push('/user')
  if (cmd === 'logout') confirmLogout()
}

const confirmLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    logout().finally(() => {
      userStore.setUserInfo(null)
      localStorage.clear()
      ElMessage.success('已退出登录')
      router.push('/login')
    })
  })
}

const handleLogin = () => router.push('/login')

const goHome = () => router.push('/home')

const handleScroll = () => {
  if (!navBarRef.value) return
  navBarRef.value.classList.toggle('with-shadow', window.scrollY > 10)
}
</script>

<style scoped>
.main-nav {
  position: fixed;
  top: 0;
  left: 0;
  height: 72px;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  z-index: 999;
  padding: 0 20px;
}
.with-shadow {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.98);
}

/* logo */
.logo-box {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.2s ease;
  padding: 8px 16px;
  border-radius: 12px;
}
.logo-box:hover {
  background: rgba(33, 150, 243, 0.05);
  opacity: 0.9;
}
.logo-icon-box {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2196F3, #1976D2);
  border-radius: 12px;
  transition: all 0.3s ease;
}
.logo-box:hover .logo-icon-box {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}
.logo-icon-box svg {
  width: 24px;
  height: 24px;
  color: white;
}
.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  letter-spacing: 1px;
  background: linear-gradient(90deg, #2196F3, #1976D2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC', sans-serif;
}

/* 导航栏 */
.nav-list {
  display: flex;
  list-style: none;
  gap: 8px;
  margin: 0 40px;
}
.nav-list li {
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 8px;
}
.nav-list li a {
  text-decoration: none;
  color: #4a5568;
  font-weight: 500;
  font-size: 14px;
  padding: 10px 16px;
  transition: all 0.2s ease;
  display: block;
  border-radius: 8px;
}

/* hover 效果 */
.nav-list li:hover a {
  color: #2196F3;
  background: rgba(33, 150, 243, 0.08);
}

/* 下划线指示器 */
.nav-list li::after {
  content: '';
  position: absolute;
  bottom: 8px;
  left: 50%;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #2196F3, #1976D2);
  transform: translateX(-50%);
  transition: width 0.3s ease;
}
.nav-list li:hover::after,
.nav-list li.active::after {
  width: 60%;
}

/* ✅ 激活状态高亮 */
.nav-list li.active a {
  color: #2196F3;
  font-weight: 600;
  background: rgba(33, 150, 243, 0.12);
  position: relative;
  z-index: 1;
}

/* 用户区 */
.user-section,
.auth-section {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-name {
  margin-right: 8px;
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
}
:deep(.el-dropdown-link) {
  display: flex;
  align-items: center;
  gap: 6px;
}
.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  transition: all 0.2s ease;
  border: 2px solid transparent;
}
.user-avatar:hover {
  transform: scale(1.08);
  border-color: #2196F3;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
}
.default-avatar {
  font-size: 32px;
  color: #2196F3;
}

.el-button {
  font-size: 14px;
  padding: 8px 18px;
  border-radius: 8px;
  transition: all 0.2s ease;
  font-weight: 500;
}
.el-button--success {
  background: linear-gradient(135deg, #2196F3, #1976D2);
  border: none;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.2);
}
.el-button--success:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}
.el-button--success.plain {
  background: transparent;
  color: #2196F3;
  border: 1.5px solid #2196F3;
}
.el-button--success.plain:hover {
  background: rgba(33, 150, 243, 0.08);
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-list {
    display: none;
  }

  .logo-text {
    font-size: 18px;
  }

  .logo-icon-box {
    width: 36px;
    height: 36px;
  }

  .auth-section .el-button {
    padding: 6px 12px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .main-nav {
    height: 64px;
    padding: 0 12px;
  }

  .logo-box {
    padding: 6px 12px;
  }

  .auth-section {
    gap: 4px;
  }

  .user-name {
    display: none;
  }

  .auth-section .el-button {
    padding: 6px 10px;
    font-size: 12px;
  }
}
</style>
