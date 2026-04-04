<template>
  <div class="article-page">
    <nav2 />

    <div class="container">
      <!-- 侧边栏分类 -->
      <aside class="sidebar">
        <div class="sidebar-title">文章分类</div>
        <ul class="type-list">
          <li :class="{ active: selectedTypeId === null }" @click="selectType(null)">
            全部
          </li>
          <li
            v-for="type in articleTypes"
            :key="type.typeId"
            :class="{ active: selectedTypeId === type.typeId }"
            @click="selectType(type.typeId)"
          >
            {{ type.typeName }}
          </li>
        </ul>
      </aside>

      <!-- 主内容区 -->
      <main class="content-area">
        <!-- 文章列表 -->
        <div class="article-list">
          <div
            v-for="article in articles"
            :key="article.id"
            class="article-card"
            @click="viewDetail(article)"
          >
            <div class="card-header">
              <h3 class="article-title">{{ article.title }}</h3>
              <div class="card-meta">
                <span class="type">{{ typeMap[article.typeId] || '未知类型' }}</span>
                <span class="author">👤 {{ article.author }}</span>
              </div>
            </div>

            <div class="card-content">
              <p class="desc">
                {{ article.content.replace(/<[^>]+>/g, '').slice(0, 150) + '...' }}
              </p>
            </div>

            <div class="card-footer">
              <span class="time">📅 {{ article.createTime }}</span>
              <el-button type="primary" size="small" @click.stop="viewDetail(article)">
                阅读全文
              </el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="articles.length === 0" class="empty-state">
          <div class="empty-icon">📰</div>
          <p>暂无文章</p>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-if="totalArticles > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="totalArticles"
            :page-sizes="[10, 20, 30]"
            layout="total, sizes, prev, pager, next"
            @current-change="onPageChange"
            @size-change="handleSizeChange"
          />
        </div>

        <!-- 文章详情对话框 -->
        <el-dialog
          v-model="showDetail"
          width="800px"
          class="detail-dialog"
          :close-on-click-modal="true"
        >
          <template #header>
            <div class="dialog-header">
              <h2 class="dialog-title">{{ detail.title }}</h2>
              <div class="dialog-meta">
                <span>👤 {{ detail.author }}</span>
                <span>📅 {{ detail.createTime }}</span>
              </div>
            </div>
          </template>

          <div class="detail-content" v-html="detail.content"></div>
        </el-dialog>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import nav2 from '@/components/nav2.vue'
import { articlesPage, getArticleInfo } from '@/api/admin/articles.js'
import { articleTypesPage } from '@/api/admin/articleType.js'
import { ElMessage } from 'element-plus'

// 状态
const articleTypes = ref([])
const articles = ref([])
const typeMap = ref({})
const selectedTypeId = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalArticles = ref(0)
const showDetail = ref(false)
const detail = ref({})

// 加载类型并构建映射
async function loadTypes() {
  try {
    const res = await articleTypesPage({ pageNum: 1, pageSize: 100 })
    if (res?.code === 200) {
      articleTypes.value = res.data.data
      typeMap.value = articleTypes.value.reduce((map, t) => {
        map[t.typeId] = t.typeName
        return map
      }, {})
    }
  } catch (e) {
    console.error('类型加载失败', e)
  }
}

// 加载文章
async function loadArticles() {
  try {
    const params = { pageNum: currentPage.value, pageSize: pageSize.value }
    if (selectedTypeId.value !== null) {
      params.typeId = selectedTypeId.value
    }
    const res = await articlesPage(params)
    if (res?.code === 200) {
      articles.value = res.data.data
      totalArticles.value = res.data.total
    }
  } catch (e) {
    ElMessage.error('文章加载失败')
  }
}

// 通过ID获取文章详情
async function fetchArticleDetail(articleId) {
  try {
    const res = await getArticleInfo(articleId)
    if (res?.code === 200) {
      return res.data
    }
  } catch (e) {
    ElMessage.error('文章详情加载失败')
  }
  return null
}

// 选择类型
function selectType(typeId) {
  selectedTypeId.value = typeId
  currentPage.value = 1
}

// 分页处理
function onPageChange(page) {
  currentPage.value = page
}

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
}

// 显示详情
async function viewDetail(article) {
  const articleDetail = await fetchArticleDetail(article.id)
  if (articleDetail) {
    detail.value = articleDetail
    showDetail.value = true
  }
}

// 监听变化
watch([selectedTypeId, currentPage], loadArticles)

// 初始加载
onMounted(async () => {
  await loadTypes()
  await loadArticles()
})
</script>

<style scoped>
/* Container */
.article-page {
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

/* Article List */
.article-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e9ecef;
}

.article-card:hover {
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

.article-title {
  font-size: 22px;
  color: #212529;
  font-weight: 700;
  margin: 0 0 8px 0;
  line-height: 1.4;
  flex: 1;
}

.card-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.type {
  background: #e9ecef;
  color: #495057;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.author {
  color: #666;
  font-size: 13px;
}

/* Card Content */
.card-content {
  margin-bottom: 16px;
}

.desc {
  color: #495057;
  font-size: 15px;
  line-height: 1.8;
  margin: 0;
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

/* Detail Dialog */
.detail-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e9ecef;
}

.dialog-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dialog-title {
  font-size: 24px;
  font-weight: 700;
  color: #212529;
  margin: 0;
}

.dialog-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.detail-dialog :deep(.el-dialog__body) {
  padding: 24px;
  max-height: 60vh;
  overflow-y: auto;
}

.detail-content {
  line-height: 1.8;
  color: #333;
  font-size: 16px;
}

.detail-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 16px 0;
}

.detail-content :deep(p) {
  margin: 16px 0;
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

  .article-card {
    padding: 16px;
  }

  .article-title {
    font-size: 18px;
  }

  .desc {
    font-size: 14px;
  }

  .detail-dialog {
    width: 95% !important;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .container {
    padding: 16px;
  }
}
</style>
