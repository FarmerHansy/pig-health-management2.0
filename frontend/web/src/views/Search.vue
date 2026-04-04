<template>
  <div class="search-page">
    <nav2 />

    <div class="container">
      <!-- 标题 -->
      <div class="page-title">
        <h2>搜索结果</h2>
        <p v-if="searchQuery" class="search-query">"{{ searchQuery }}"</p>
      </div>

      <!-- 搜索结果列表 -->
      <div class="result-list">
        <div
          v-for="item in displayedItems"
          :key="item.id"
          class="result-card"
        >
          <div class="card-header">
            <h3 class="illness-name">{{ item.illnessName }}</h3>
            <div class="card-meta">
              <span class="category">{{ getCategoryNameById(item.kindId) }}</span>
              <span class="views">
                <svg class="eye-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                {{ getCategoryPageviewById(item.id) }}
              </span>
            </div>
          </div>

          <div class="card-content">
            <div class="info-row">
              <span class="info-label">症状</span>
              <p class="info-text">{{ stripHtml(item.illnessSymptom) }}</p>
            </div>

            <div class="info-row">
              <span class="info-label">原因</span>
              <p class="info-text">{{ stripHtml(item.includeReason) }}</p>
            </div>

            <div class="info-row">
              <span class="info-label">特殊症状</span>
              <p class="info-text special">{{ stripHtml(item.specialSymptom) }}</p>
            </div>

            <div class="info-row" v-if="getMedicinesByIllnessId(item.id).length">
              <span class="info-label">推荐药物</span>
              <div class="medicine-list">
                <span
                  v-for="(medicine, index) in getMedicinesByIllnessId(item.id)"
                  :key="index"
                  class="medicine-item"
                  @click.stop="handleViewCombinedInfo(item.id, medicine.id)"
                >
                  {{ medicine.name }}
                </span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <span class="time">{{ item.createTime }}</span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="displayedItems.length === 0" class="empty-state">
        <div class="empty-icon">🔍</div>
        <p>未找到相关疾病</p>
        <p class="empty-desc">请尝试更换关键词或查看其他疾病</p>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-if="totalItems > 0"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalItems"
          :page-sizes="[9, 18, 27]"
          layout="total, sizes, prev, pager, next"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>

      <!-- 综合信息模态框 -->
      <el-dialog
        v-model="showCombinedDialog"
        title="疾病与药品详情"
        width="700px"
      >
        <el-form :model="combinedForm" label-width="120px">
          <el-form-item label="疾病名称">
            <el-input v-model="combinedForm.illnessName" disabled />
          </el-form-item>
          <el-form-item label="疾病症状">
            <el-input v-model="combinedForm.illnessSymptom" type="textarea" :rows="2" disabled />
          </el-form-item>
          <el-form-item label="特殊症状">
            <el-input v-model="combinedForm.specialSymptom" type="textarea" :rows="2" disabled />
          </el-form-item>
          <el-form-item label="对症药品">
            <el-input v-model="combinedForm.medicineName" disabled />
          </el-form-item>
          <el-form-item label="作用">
            <el-input v-model="combinedForm.keyword" disabled />
          </el-form-item>
          <el-form-item label="药品功效">
            <el-input v-model="combinedForm.medicineEffect" type="textarea" :rows="2" disabled />
          </el-form-item>
          <el-form-item label="药品品牌">
            <el-input v-model="combinedForm.medicineBrand" disabled />
          </el-form-item>
          <el-form-item label="用法用量">
            <el-input v-model="combinedForm.usAge" type="textarea" :rows="2" disabled />
          </el-form-item>
          <el-form-item label="禁忌">
            <el-input v-model="combinedForm.taboo" type="textarea" :rows="2" disabled />
          </el-form-item>
          <el-form-item label="药品相互作用">
            <el-input v-model="combinedForm.interaction" type="textarea" :rows="2" disabled />
          </el-form-item>
          <el-form-item label="药品价格">
            <el-input-number v-model="combinedForm.medicinePrice" :precision="2" :step="0.01" disabled />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showCombinedDialog = false">关闭</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import nav2 from '@/components/nav2.vue'
import { illnessPage } from '@/api/admin/illness.js'
import { illnessKindPage } from '@/api/admin/illnessKind.js'
import { pageviewPage } from '@/api/admin/pageview.js'
import { ElMessage } from 'element-plus'
import { illnessMedicinePage } from '@/api/admin/illnessMedicine.js'
import { medicinePage } from '@/api/admin/medicine.js'
import DOMPurify from 'dompurify'

const route = useRoute()
const searchQuery = ref(route.query.q || '')

const illnesses = ref([])
const illnessKindData = ref([])
const pageviewPageData = ref([])
const illnessMedicinePageData = ref([])
const medicinePageData = ref([])

const pageSize = ref(9)
const currentPage = ref(1)
const totalItems = computed(() => illnesses.value.length)
const displayedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return illnesses.value.slice(start, end)
})

// 加载数据
onMounted(() => {
  Promise.all([
    loadIllnessKind(),
    loadPageview(),
    loadIllness(),
    loadIllnessMedicine(),
    loadMedicine()
  ])
})

async function loadIllnessKind() {
  try {
    const res = await illnessKindPage({ pageNum: 1, pageSize: 100 })
    if (res?.code === 200) {
      illnessKindData.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载分类失败')
  }
}

async function loadPageview() {
  try {
    const res = await pageviewPage({ pageNum: 1, pageSize: 100 })
    if (res?.code === 200) {
      pageviewPageData.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载浏览量失败')
  }
}

async function loadIllness() {
  try {
    const res = await illnessPage({ pageNum: 1, pageSize: 100, illnessName: searchQuery.value })
    if (res?.code === 200) {
      illnesses.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载疾病数据失败')
  }
}

async function loadIllnessMedicine() {
  try {
    const res = await illnessMedicinePage({ pageNum: 1, pageSize: 100 })
    if (res?.code === 200) {
      illnessMedicinePageData.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载疾病药品关系失败')
  }
}

async function loadMedicine() {
  try {
    const res = await medicinePage({ pageNum: 1, pageSize: 100 })
    if (res?.code === 200) {
      medicinePageData.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载药品数据失败')
  }
}

function handlePageChange(page) {
  currentPage.value = page
}

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
}

// 工具函数
const getCategoryNameById = (kindId) => {
  const category = illnessKindData.value.find(item => item.id === kindId)
  return category ? category.name : '未知种类'
}

const getCategoryPageviewById = (id) => {
  const pageview = pageviewPageData.value.find(item => item.illnessId === id)
  return pageview ? pageview.pageviews : 0
}

const getMedicineNameById = (medicineId) => {
  const medicine = medicinePageData.value.find(item => item.id === medicineId)
  return medicine ? medicine.medicineName : '未知药物'
}

const getMedicinesByIllnessId = (illnessId) => {
  return illnessMedicinePageData.value
    .filter(item => item.illnessId === illnessId)
    .map(item => ({
      name: getMedicineNameById(item.medicineId),
      id: item.medicineId
    }))
}

const stripHtml = (html) => {
  if (!html) return ''
  return DOMPurify.sanitize(html, { ALLOWED_TAGS: [] })
}

// 综合信息模态框
const showCombinedDialog = ref(false)
const combinedForm = ref({})

const handleViewCombinedInfo = (illnessId, medicineId) => {
  const illness = illnesses.value.find(item => item.id === illnessId)
  const medicine = medicinePageData.value.find(item => item.id === medicineId)

  if (!illness || !medicine) {
    ElMessage.error('获取信息失败')
    return
  }

  combinedForm.value = {
    illnessName: illness.illnessName,
    illnessSymptom: illness.illnessSymptom,
    specialSymptom: illness.specialSymptom,
    medicineName: medicine.medicineName,
    keyword: medicine.keyword,
    medicineEffect: medicine.medicineEffect,
    medicineBrand: medicine.medicineBrand,
    usAge: medicine.usAge,
    taboo: medicine.taboo,
    interaction: medicine.interaction,
    medicinePrice: medicine.medicinePrice,
  }

  showCombinedDialog.value = true
}
</script>

<style scoped>
/* Container */
.search-page {
  background: #fff;
  min-height: calc(100vh - 60px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* Page Title */
.page-title {
  text-align: center;
  margin-bottom: 40px;
}

.page-title h2 {
  font-size: 32px;
  color: #212529;
  font-weight: 700;
  margin: 0 0 8px 0;
}

.search-query {
  color: #666;
  font-size: 16px;
  margin: 0;
}

/* Result List */
.result-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.result-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
  transition: all 0.3s;
  border: 1px solid #e9ecef;
}

.result-card:hover {
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

.illness-name {
  font-size: 20px;
  color: #212529;
  font-weight: 700;
  margin: 0 0 8px 0;
  line-height: 1.3;
  flex: 1;
}

.card-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.category {
  background: #e9ecef;
  color: #495057;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.views {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 13px;
}

.eye-icon {
  width: 16px;
  height: 16px;
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
  min-width: 80px;
  color: #6c757d;
  font-weight: 500;
  font-size: 14px;
}

.info-text {
  flex: 1;
  color: #495057;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
  word-break: break-word;
}

.info-text.special {
  color: #dc3545;
}

.medicine-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.medicine-item {
  background: #409eff;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.medicine-item:hover {
  background: #66b1ff;
  transform: translateY(-1px);
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

/* Empty State */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.3;
}

.empty-state p {
  font-size: 16px;
  color: #666;
  margin: 8px 0;
}

.empty-desc {
  font-size: 14px;
  opacity: 0.7;
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* Responsive */
@media (max-width: 768px) {
  .container {
    padding: 20px 16px;
  }

  .page-title h2 {
    font-size: 24px;
  }

  .result-list {
    grid-template-columns: 1fr;
  }

  .illness-name {
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
  .result-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>