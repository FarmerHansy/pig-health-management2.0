<template>
  <div class="home-container">
    <nav1 />
    <nav2 />

    <!-- 顶部横幅 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1>智慧养猪<br />健康未来</h1>
        <p>AI智能兽医 · 精准疾病诊断 · 科学用药指导</p>
        <el-button type="primary" @click="goToDoctor">立即咨询</el-button>
      </div>
    </section>

    <!-- 核心功能 -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">核心功能</h2>
        <div class="features-grid">
          <a class="feature-item" href="javascript:void(0)" @click="goToDoctor">
            <div class="icon">💬</div>
            <div class="feature-info">
              <h3>AI智能问诊</h3>
              <p>24小时快速诊断，专业诊疗建议</p>
            </div>
          </a>

          <a class="feature-item" href="javascript:void(0)" @click="router.push('/findIllness')">
            <div class="icon">📚</div>
            <div class="feature-info">
              <h3>疾病百科</h3>
              <p>常见猪病详细资料与治疗方法</p>
            </div>
          </a>

          <a class="feature-item" href="javascript:void(0)" @click="router.push('/findMedicines')">
            <div class="icon">💊</div>
            <div class="feature-info">
              <h3>药品中心</h3>
              <p>精选优质兽药，科学配比</p>
            </div>
          </a>

          <a class="feature-item" href="javascript:void(0)" @click="router.push('/news')">
            <div class="icon">📰</div>
            <div class="feature-info">
              <h3>养殖知识</h3>
              <p>最新养殖资讯与专业文章</p>
            </div>
          </a>
        </div>
      </div>
    </section>

    <buttom2 />
  </div>
</template>

<script setup>
import nav1 from '@/components/nav1.vue'
import nav2 from '@/components/nav2.vue'
import buttom2 from '@/components/buttom2.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const goToDoctor = () => router.push('/doctor')
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #fff;
}

/* 顶部横幅 */
.hero-section {
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  color: #333;
  padding: 80px 20px;
  text-align: center;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);
}

.hero-content {
  max-width: 600px;
}

.hero-content h1 {
  font-size: 42px;
  font-weight: 500;
  margin-bottom: 16px;
  line-height: 1.2;
  color: #1a1a1a;
  letter-spacing: -0.5px;
}

.hero-content p {
  font-size: 16px;
  margin-bottom: 30px;
  color: #64748b;
  font-weight: 400;
  line-height: 1.6;
}

.hero-content .el-button {
  background: #0ea5e9;
  color: white;
  border-color: #0ea5e9;
  font-size: 16px;
  font-weight: 500;
  padding: 12px 32px;
  border-radius: 6px;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(14, 165, 233, 0.15);
}

.hero-content .el-button:hover {
  background: #0284c7;
  border-color: #0284c7;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(14, 165, 233, 0.25);
}

/* 核心功能区域 */
.features-section {
  padding: 60px 20px 80px;
  background: #ffffff;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.section-title {
  font-size: 28px;
  color: #1a1a1a;
  font-weight: 500;
  margin-bottom: 40px;
  text-align: center;
  position: relative;
  display: inline-block;
  left: 50%;
  transform: translateX(-50%);
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  height: 2px;
  background: #0ea5e9;
  border-radius: 1px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.feature-item {
  display: block;
  background: #ffffff;
  padding: 24px;
  border-radius: 8px;
  text-decoration: none;
  color: #333;
  transition: all 0.3s;
  border: 1px solid rgba(0, 0, 0, 0.03);
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.feature-item:hover {
  background: #f8fafc;
  border-color: rgba(14, 165, 233, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.icon {
  font-size: 32px;
  line-height: 1;
  color: #0ea5e9;
}

.feature-info h3 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 6px;
  color: #1a1a1a;
}

.feature-info p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 36px;
  }

  .hero-content p {
    font-size: 16px;
  }

  .hero-content .el-button {
    font-size: 16px;
    padding: 14px 32px;
    width: 100%;
    max-width: 280px;
  }

  .section-title {
    font-size: 28px;
    margin-bottom: 30px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .feature-item {
    padding: 24px;
  }

  .icon {
    font-size: 32px;
  }

  .feature-info h3 {
    font-size: 16px;
  }

  .feature-info p {
    font-size: 13px;
  }
}

@media (min-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>