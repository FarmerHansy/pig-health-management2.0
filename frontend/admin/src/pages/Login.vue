<template>
  <div class="login-container">
    <div class="background-gradient"></div>

    <div class="login-wrapper">
      <div class="login-panel">
        <!-- Logo -->
        <div class="login-logo">
          <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
            <circle cx="32" cy="32" r="30" fill="#2563eb"/>
            <path d="M32 52C43.0457 52 52 43.0457 52 32C52 20.9543 43.0457 12 32 12C20.9543 12 12 20.9543 12 32C12 43.0457 20.9543 52 32 52Z" stroke="white" stroke-width="2"/>
            <path d="M36 32C36 34.2091 34.2091 36 32 36C29.7909 36 28 34.2091 28 32C28 29.7909 29.7909 28 32 28C34.2091 28 36 29.7909 36 32Z" stroke="white" stroke-width="2"/>
            <path d="M26 26L29 29" stroke="white" stroke-width="2" stroke-linecap="round"/>
            <path d="M35 29L38 26" stroke="white" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <h1>Pig Health Admin</h1>
          <p>智慧养猪医药管理系统</p>
        </div>

        <!-- 表单 -->
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="userAccount" class="form-label">账号</label>
            <input
              type="text"
              id="userAccount"
              v-model="userAccount"
              required
              placeholder="admin"
              class="form-input"
              autocomplete="username"
            />
          </div>

          <div class="form-group">
            <label for="password" class="form-label">密码</label>
            <input
              type="password"
              id="password"
              v-model="password"
              required
              placeholder="••••••"
              class="form-input"
              autocomplete="current-password"
            />
          </div>

          <div class="form-group">
            <label for="captcha" class="form-label">验证码</label>
            <div class="captcha-wrapper">
              <input
                type="text"
                id="captcha"
                placeholder="输入验证码"
                v-model="captchaInput"
                class="form-input"
                style="flex: 3"
                maxlength="6"
              />
              <div class="captcha-display" @click="generateCaptcha">
                <span>{{ captcha }}</span>
              </div>
            </div>
          </div>

          <div class="form-actions">
            <label class="remember-label">
              <input type="checkbox" id="remember" />
              <span>记住我</span>
            </label>
            <button type="submit" class="login-btn">
              <span>登录</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M5 12H3m8-7L3 12m8 7L3 12m16 0h-2" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>
        </form>

        <!-- 底部信息 -->
        <div class="login-footer">
          <div class="footer-divider">
            <span>技术支持</span>
          </div>
          <div class="footer-links">
            <a href="#" target="_blank">用户协议</a>
            <span class="divider">|</span>
            <a href="#" target="_blank">隐私政策</a>
          </div>
          <p class="copyright">© 2026 PigHealthSmartMedicine</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login, getuserInfo } from '@/api/user.js';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';

const userAccount = ref('admin');
const password = ref('123456');
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
      // 登录成功跳转到首页
      router.push('/articleTypeMgt');
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
.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.background-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 70%);
}

.login-wrapper {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

.login-panel {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  padding: 40px 40px 30px;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-logo {
  text-align: center;
  margin-bottom: 30px;
}

.login-logo svg {
  margin-bottom: 16px;
  filter: drop-shadow(0 4px 12px rgba(37, 99, 235, 0.2));
}

.login-logo h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  margin-bottom: 8px;
}

.login-logo p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.login-form {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 15px;
  color: #374151;
  background: #f9fafb;
  transition: all 0.3s ease;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.form-input:focus {
  outline: none;
  border-color: #2563eb;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
  transform: translateY(-1px);
}

.form-group {
  margin-bottom: 20px;
}

.captcha-wrapper {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-display {
  flex: 2;
  height: 48px;
  cursor: pointer;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  color: #fff;
  font-weight: 700;
  font-size: 20px;
  letter-spacing: 3px;
  user-select: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  transition: all 0.3s ease;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.captcha-display:hover {
  transform: scale(1.05) rotate(2deg);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.remember-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.remember-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #2563eb;
}

.remember-label span {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

.login-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn svg {
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateX(-10px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.footer-divider {
  color: #9ca3af;
  font-size: 13px;
  margin-bottom: 12px;
  position: relative;
  padding-bottom: 12px;
}

.footer-divider::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 2px;
  background: #2563eb;
}

.footer-links {
  margin-bottom: 12px;
  display: flex;
  justify-content: center;
  gap: 16px;
  font-size: 13px;
}

.footer-links a {
  color: #6b7280;
  text-decoration: none;
  transition: all 0.3s ease;
}

.footer-links a:hover {
  color: #2563eb;
  text-decoration: underline;
}

.divider {
  color: #d1d5db;
}

.copyright {
  color: #9ca3af;
  font-size: 12px;
  margin: 0;
}

@media (max-width: 480px) {
  .login-panel {
    padding: 30px 25px;
  }

  .login-logo h1 {
    font-size: 24px;
  }

  .login-form {
    margin-bottom: 15px;
  }

  .form-actions {
    flex-direction: column;
    gap: 12px;
  }

  .login-btn {
    width: 100%;
    justify-content: center;
  }

  .footer-links {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
