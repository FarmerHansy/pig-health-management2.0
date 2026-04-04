<template>
  <div class="medicine-page">
    <nav2 />

    <div class="container">
      <!-- 侧边栏分类 -->
      <aside class="sidebar">
        <div class="sidebar-title">药品分类</div>
        <ul class="type-list">
          <li :class="{ active: currentTag === '' }" @click="selectTag('')">
            全部
          </li>
          <li
            v-for="t in allTags"
            :key="t"
            :class="{ active: currentTag === t }"
            @click="selectTag(t)"
          >
            {{ t }}
          </li>
        </ul>
      </aside>

      <!-- 主内容区 -->
      <main class="content-area">
        <!-- 顶部搜索 -->
        <div class="top-controls">
          <el-input
            v-model="searchText"
            placeholder="搜索药品名称或功效..."
            clearable
            @clear="onSearchClear"
            @input="onSearchInput"
            class="search-input"
            prefix-icon="Search"
          />
          <div class="stats">{{ filteredList.length }} 条结果</div>
        </div>

        <!-- 药品列表 -->
        <div class="medicine-list">
          <div
            v-for="medicine in pagedList"
            :key="medicine.id"
            class="medicine-card"
            @click="openDetail(medicine.id)"
          >
            <div class="card-header">
              <h3 class="medicine-name">{{ medicine.medicineName }}</h3>
              <div class="card-meta">
                <span class="price">¥{{ Number(medicine.medicinePrice).toFixed(2) }}</span>
                <span class="type" :class="getTypeClass(medicine.medicineType)">
                  {{ getMedicineTypeName(medicine.medicineType) }}
                </span>
              </div>
            </div>

            <div class="card-content">
              <div class="info-row">
                <span class="info-label">功效</span>
                <p class="info-text">{{ medicine.medicineEffect }}</p>
              </div>

              <div class="info-row">
                <span class="info-label">禁忌</span>
                <p class="info-text taboo">{{ medicine.taboo }}</p>
              </div>
            </div>

            <div class="card-footer">
              <span class="brand">{{ medicine.medicineBrand }}</span>
              <el-button type="primary" size="small" @click.stop="openDetail(medicine.id)">
                详情
              </el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="filteredList.length === 0" class="empty-state">
          <div class="empty-icon">💊</div>
          <p>暂无相关药品</p>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-if="filteredList.length > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="filteredList.length"
            :page-sizes="[10, 20, 30]"
            layout="total, sizes, prev, pager, next"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import nav2 from '@/components/nav2.vue'
import { medicinePage } from '@/api/admin/medicine.js'
import debounce from 'lodash/debounce'

const router = useRouter()
const currentTag = ref('')
const searchText = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const medicines = ref([])

// 初始化数据
onMounted(() => {
  fetchData()
})

// 获取数据
async function fetchData() {
  try {
    const res = await medicinePage({ pageNum: 1, pageSize: 999 })
    if (res?.code === 200) {
      medicines.value = res.data.data || []
    }
  } catch (e) {
    console.error('获取药品数据失败', e)
  }
}

// 搜索防抖
const debouncedSearch = debounce(() => {
  currentPage.value = 1
}, 300)

function onSearchInput() {
  debouncedSearch()
}

function onSearchClear() {
  searchText.value = ''
  currentPage.value = 1
}

// 标签筛选
function selectTag(tag) {
  currentTag.value = tag
  currentPage.value = 1
}

// 分页处理
function handlePageChange(page) {
  currentPage.value = page
}

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
}

// 跳转详情
function openDetail(id) {
  router.push({ name: 'MedicineDetail', params: { id } })
}

// 计算标签列表
const allTags = computed(() => {
  const set = new Set()
  medicines.value.forEach((it) => {
    (it.keyword || '').split(',').forEach(k => {
      const s = k.trim()
      if (s) set.add(s)
    })
  })
  return Array.from(set)
})

// 筛选列表
const filteredList = computed(() => {
  const list = medicines.value || []
  const q = searchText.value.trim().toLowerCase()
  const tag = currentTag.value

  return list.filter(item => {
    const matchTag = !tag || (item.keyword || '').split(',').map(s => s.trim()).includes(tag)
    const matchSearch = !q ||
      (item.medicineName || '').toLowerCase().includes(q) ||
      (item.medicineEffect || '').toLowerCase().includes(q)
    return matchTag && matchSearch
  })
})

// 分页列表
const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredList.value.slice(start, end)
})

// 工具函数
const getMedicineTypeName = (t) => {
  switch (+t) {
    case 0: return '西药'
    case 1: return '中药'
    case 2: return '中成药'
    default: return '未知'
  }
}

const getTypeClass = (t) => {
  if (t === 0) return 'western'
  if (t === 1) return 'chinese'
  return 'neutral'
}
</script>

<style scoped>
/* Container */
.medicine-page {
  background: #fff;
  min-height: calc(100vh - 60px);
}

/* Layout */
.container {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  gap: 24px;
}

/* Sidebar */
.sidebar {
  width: 200px;
  flex-shrink: 0;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding: 8px 0;
  border-bottom: 1px solid #e9ecef;
}

.type-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.type-list li {
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #555;
  font-size: 14px;
}

.type-list li:hover {
  background: #f5f5f5;
  color: #10b981;
}

.type-list li.active {
  background: #10b981;
  color: white;
  font-weight: 500;
}

/* Content Area */
.content-area {
  flex: 1;
}

/* Top Controls */
.top-controls {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: none;
  border: 1px solid #e9ecef;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #10b981;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #10b981;
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.1);
}

.stats {
  color: #666;
  font-size: 14px;
  white-space: nowrap;
}

/* Medicine List */
.medicine-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.medicine-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e9ecef;
}

.medicine-card:hover {
  background: white;
  border-color: #10b981;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e9ecef;
}

.medicine-name {
  font-size: 20px;
  color: #212529;
  font-weight: 700;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.card-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.price {
  color: #f56c6c;
  font-weight: 700;
  font-size: 20px;
}

.type {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.type.western {
  background: #e3f2fd;
  color: #1976d2;
}

.type.chinese {
  background: #e8f5e9;
  color: #2e7d32;
}

.type.neutral {
  background: #fff8e1;
  color: #e65100;
}

/* Card Content */
.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  gap: 12px;
}

.info-label {
  min-width: 60px;
  color: #6c757d;
  font-weight: 500;
}

.info-text {
  flex: 1;
  color: #495057;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
  word-break: break-word;
}

.taboo {
  color: #dc3545;
}

/* Card Footer */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e9ecef;
  color: #999;
  font-size: 13px;
}

.brand {
  color: #666;
}

.card-footer .el-button {
  border-radius: 6px;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.3;
}

.empty-state p {
  font-size: 16px;
  color: #666;
}

/* Pagination */
.pagination-container {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

/* Responsive */
@media (max-width: 768px) {
  .container {
    flex-direction: column;
    padding: 16px;
  }

  .sidebar {
    width: 100%;
  }

  .type-list {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .type-list li {
    padding: 8px 16px;
  }

  .top-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .stats {
    text-align: right;
  }

  .medicine-card {
    padding: 16px;
  }

  .medicine-name {
    font-size: 18px;
  }

  .info-row {
    flex-direction: column;
  }

  .info-label {
    min-width: auto;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .container {
    padding: 16px;
  }
}
</style>
