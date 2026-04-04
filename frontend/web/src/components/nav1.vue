<template>
  <div class="announcement-bar">
    <p>✨ 智慧养殖，健康未来 | 专业兽医在线咨询</p>
    <el-button type="success" class="search-btn" @click="handleClick">
      <el-icon><Search /></el-icon>
      快速搜索
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElButton, ElIcon } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const handleClick = () => {
  // 搜索功能
}
</script>

<style scoped>
.announcement-bar {
  width: 100%;
  height: 55px;
  background: linear-gradient(135deg, #10b981, #0da271);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.2);
}

.announcement-bar p {
  font-size: 15px;
  line-height: 1.5;
  margin: 0;
  font-weight: 400;
  letter-spacing: 0.5px;
}

.search-btn {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: white;
  font-size: 14px;
  padding: 8px 20px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.search-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.el-icon {
  color: white;
  font-size: 16px;
  margin-right: 6px;
}
</style>