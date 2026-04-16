<template>
  <div class="login-container">
    <!-- 简约登录卡片 -->
    <div class="login-card">
      <!-- Logo区域 -->
      <div class="logo-container">
        <div class="logo-icon">🐷</div>
        <h1 class="logo-text">猪健康管理</h1>
      </div>

      <!-- 登录表单 -->
      <div class="login-form">
        <h2 class="login-title">用户登录</h2>
        <p class="login-subtitle">请使用您的账号登录系统</p>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="userAccount">用户名</label>
            <input type="text" id="userAccount" v-model="userAccount" required placeholder="请输入用户名">
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" v-model="password" required placeholder="请输入密码">
          </div>
          <div class="form-group captcha-group">
            <label for="captcha">验证码</label>
            <div class="captcha-wrapper">
              <input type="text" id="captcha" placeholder="输入验证码" v-model="captchaInput">
              <div class="captcha-display" @click="generateCaptcha">{{ captcha }}</div>
            </div>
          </div>
          <div class="remember-password">
            <input type="checkbox" id="remember">
            <label for="remember">记住我</label>
          </div>
          <button type="submit" class="login-button">立即登录</button>
        </form>

        <!-- 其他登录方式 -->
        <!-- <div class="other-login-methods">
          <p class="other-login-title">其他方式登录</p>
          <div class="social-login">
            <img src="@/assets/images/icons/wechat.png" alt="微信登录">
            <img src="@/assets/images/icons/alipay.png" alt="支付宝登录">
            <img src="@/assets/images/icons/douyin.png" alt="抖音登录">
            <img src="@/assets/images/icons/qq.png" alt="QQ登录">
            <img src="@/assets/images/icons/sina.png" alt="微博登录">
            <img src="@/assets/images/icons/github.png" alt="GitHub登录">
            <img src="@/assets/images/icons/google.png" alt="Google登录">
          </div>
        </div> -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login, getuserInfo } from '@/api/admin/user.js';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';

const userAccount = ref('');
const password = ref('');
const captchaInput = ref('');
const captcha = ref('');

// 生成随机验证码
const generateCaptcha = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < 6; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  captcha.value = result;
};

// 初始化生成验证码
generateCaptcha();

const router = useRouter();
const userStore = useUserStore();

const handleLogin = async () => {
  // 验证码验证
  if (captchaInput.value.toLowerCase() !== captcha.value.toLowerCase()) {
    ElMessage.error('验证码错误');
    generateCaptcha(); // 刷新验证码
    return;
  }

  try {
    const response = await login(userAccount.value, password.value);
    if (response.code === 200) {
      ElMessage.success('登录成功');
      // 存储 token 到本地存储
      localStorage.setItem('token', response.data.tokenValue);
      localStorage.setItem('userId', response.data.loginId);
      // 获取 loginId
      const loginId = response.data.loginId;
      // 调用 getuserInfo 接口获取用户信息
      const userInfoResponse = await getuserInfo(loginId);
      if (userInfoResponse.code === 200) {
        // 将用户信息存储到 Pinia 中
        userStore.setUserInfo(userInfoResponse.data);
        // 将用户信息存储到 localStorage 中
        localStorage.setItem('userInfo', JSON.stringify(userInfoResponse.data));
      }
      // 跳转到首页
      router.push({ name: 'Home' });
    } else {
      ElMessage.error(response.message || '请求失败，请稍后再试');
    }
  } catch (error) {
    console.error('登录失败:', error);
    ElMessage.error('登录失败，请重试');
  }
};
</script>

<style scoped>
/* 简约登录容器 */
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ef 100%);
  padding: 20px;
}

/* Logo容器 */
.logo-container {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 10px;
  color: #2196F3;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
  letter-spacing: 2px;
}

/* 简约登录卡片 */
.login-card {
  width: 100%;
  max-width: 420px;
  background-color: white;
  border-radius: 12px;
  padding: 45px 40px;
  box-shadow: 0 8px 30px rgba(15, 103, 250, 0.12);
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 登录标题 */
.login-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2c3e50;
  text-align: center;
}

/* 登录副标题 */
.login-subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 30px;
  text-align: center;
}

/* 表单分组 */
.form-group {
  margin-bottom: 20px;
}

/* 标签样式 */
label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4a5568;
  font-size: 14px;
}

/* 输入框样式 */
input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 12px 16px;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  color: #4a5568;
  background-color: #f8fafc;
}

input[type="text"]:focus,
input[type="password"]:focus {
  border-color: #2196F3;
  outline: none;
  background-color: white;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
}

input[type="text"]:hover,
input[type="password"]:hover {
  border-color: #cbd5e0;
}

/* 验证码区域 */
.captcha-group {
  margin-bottom: 20px;
}

.captcha-wrapper {
  display: flex;
  gap: 12px;
}

.captcha-wrapper input {
  flex: 1;
}

.captcha-display {
  height: 42px;
  min-width: 120px;
  cursor: pointer;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2196F3, #1976D2);
  color: white;
  font-weight: 600;
  letter-spacing: 2px;
  user-select: none;
  transition: all 0.3s;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.3);
}

.captcha-display:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.4);
}

.captcha-display:active {
  transform: scale(0.95);
}

/* 记住密码 */
.remember-password {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  gap: 8px;
}

.remember-password input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.remember-password label {
  font-size: 14px;
  color: #718096;
  cursor: pointer;
}

.remember-password input[type="checkbox"]:checked {
  background-color: #2196F3;
  border-color: #2196F3;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  padding: 13px;
  background: linear-gradient(135deg, #2196F3, #1976D2);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(33, 150, 243, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.2);
}

/* 底部链接 */
.footer-links {
  text-align: center;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #edf2f7;
}

.footer-links p {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0;
}

.footer-links a {
  color: #2196F3;
  text-decoration: none;
  font-weight: 500;
  margin-left: 8px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #1976D2;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 35px 30px;
    margin: 10px;
  }

  .logo-icon {
    font-size: 40px;
  }

  .logo-text {
    font-size: 18px;
  }

  .login-title {
    font-size: 20px;
  }

  .captcha-wrapper {
    flex-direction: column;
  }

  .captcha-display {
    width: 100%;
  }
}
</style>