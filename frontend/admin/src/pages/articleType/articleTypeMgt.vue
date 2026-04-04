<template>
  <div class="article-type-mgt">
    <el-card class="header-card">
      <div class="page-header">
        <h2 class="page-title">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
          </svg>
          <span>文章类型管理</span>
        </h2>
        <el-button
          type="primary"
          @click="addBtnClick"
          class="add-btn"
        >
          <el-icon><Plus /></el-icon>
          <span>添加文章类型</span>
        </el-button>
      </div>
    </el-card>

    <el-card class="content-card">
      <div class="table-container">
        <div class="search-section">
          <div class="search-wrapper" :class="{ expanded: showSearchRow }">
            <div class="search-form">
              <el-input
                v-model="searchForm.typeName"
                placeholder="输入文章类型名称搜索"
                @input="handleSearch"
                clearable
                class="search-input"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </div>

          <div class="actions">
            <el-button
              circle
              @click="getPageList(false)"
              title="刷新"
            >
              <el-icon><Refresh /></el-icon>
            </el-button>
            <el-button
              circle
              @click="showSearchRow = !showSearchRow"
              :title="showSearchRow ? '收起搜索' : '展开搜索'"
            >
              <el-icon>
                <ArrowUp v-if="showSearchRow" />
                <ArrowDown v-else />
              </el-icon>
            </el-button>
          </div>
        </div>

        <div class="table-wrapper">
          <el-table
            :data="datatable.records"
            :loading="datatable.loading"
            border
            style="width: 100%"
            :header-cell-style="{ background: '#f8f9fa', color: '#4b5563', fontWeight: 600 }"
            :row-style="{ background: '#fff' }"
            class="modern-table"
          >
            <el-table-column
              prop="typeName"
              label="文章类型名称"
              align="center"
              min-width="200"
            >
              <template #default="{ row }">
                <span class="type-name">{{ row.typeName }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              align="center"
              width="280"
              fixed="right"
              class-name="action-column"
            >
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button
                    type="info"
                    size="small"
                    @click="detailBtnClick(row.typeId)"
                    class="action-btn detail-btn"
                  >
                    <el-icon><View /></el-icon>
                    <span>详情</span>
                  </el-button>
                  <el-button
                    type="primary"
                    size="small"
                    @click="updateBtnClick(row.typeId)"
                    class="action-btn edit-btn"
                  >
                    <el-icon><Edit /></el-icon>
                    <span>编辑</span>
                  </el-button>
                  <el-popconfirm
                    title="确认删除该类型？"
                    @confirm="deleteBtnOkClick(row.typeId)"
                    confirm-button-text="确定"
                    cancel-button-text="取消"
                  >
                    <template #reference>
                      <el-button
                        type="danger"
                        size="small"
                        class="action-btn delete-btn"
                      >
                        <el-icon><Delete /></el-icon>
                        <span>删除</span>
                      </el-button>
                    </template>
                  </el-popconfirm>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="pagination-wrapper" v-if="datatable.total > 0">
          <el-pagination
            v-model:current-page="searchForm.pageNum"
            v-model:page-size="searchForm.pageSize"
            :total="datatable.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
            class="modern-pagination"
          />
        </div>
      </div>
    </el-card>

    <!-- 添加/修改弹窗 -->
    <el-dialog
      v-model="modal.visible"
      :title="modal.title"
      width="500px"
      :close-on-click-modal="false"
      draggable
      class="modern-dialog"
    >
      <component
        :is="modal.component"
        :params="modal.params"
        @ok="onOk"
        @cancel="onCancel"
        v-if="modal.visible"
      />
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailModal.visible"
      :title="detailModal.title"
      width="500px"
      draggable
      class="modern-dialog"
    >
      <component
        :is="detailModal.component"
        :params="detailModal.params"
        @cancel="detailOnCancel"
        v-if="detailModal.visible"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, shallowRef, onMounted } from 'vue';
import { articleTypesAdd, articleTypesPage, articleTypesDelete } from '@/api/articleType.js';
import articleTypeEdit from './articleTypeEdit.vue';
import articleTypeDetail from './articleTypeDetail.vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh, Plus, ArrowUp, ArrowDown, Edit, Delete, View } from '@element-plus/icons-vue';

const allData = ref([]);

// 模糊搜索处理
const handleSearch = () => {
  const q = searchForm.typeName?.trim().toLowerCase();
  if (!q) {
    datatable.records = allData.value;
    datatable.total = allData.value.length;
    return;
  }

  datatable.records = allData.value.filter(item =>
    item.typeName && item.typeName.toLowerCase().includes(q)
  );
  datatable.total = datatable.records.length;
};

const resetSearch = () => {
  searchForm.typeName = null;
  datatable.records = allData.value;
  datatable.total = allData.value.length;
};

const showSearchRow = ref(true);
const searchForm = reactive({
  typeName: null,
  pageNum: 1,
  pageSize: 10
});

const datatable = reactive({
  records: [],
  loading: false,
  total: 0
});

const getPageList = (isReset = false) => {
  if (isReset) {
    searchForm.typeName = null;
    searchForm.pageNum = 1;
    searchForm.pageSize = 10;
  }
  datatable.loading = true;

  articleTypesPage(searchForm)
    .then(res => {
      if (res.code === 200) {
        datatable.records = res.data.data;
        allData.value = res.data.data;
        datatable.total = res.data.total;
      } else {
        ElMessage.error(res.message || '获取文章类型列表失败');
      }
    })
    .finally(() => {
      datatable.loading = false;
    });
};

const handlePageChange = (pageNum) => {
  searchForm.pageNum = pageNum;
  getPageList();
};

const handleSizeChange = (pageSize) => {
  searchForm.pageSize = pageSize;
  getPageList();
};

const modal = reactive({
  visible: false,
  title: '文章类型管理',
  params: {},
  component: null
});

const detailModal = reactive({
  visible: false,
  title: '文章类型详情',
  params: {},
  component: null
});

const addBtnClick = () => {
  modal.visible = true;
  modal.title = '添加文章类型';
  modal.params = { operationType: 'add' };
  modal.component = shallowRef(articleTypeEdit);
};

const updateBtnClick = (typeId) => {
  modal.visible = true;
  modal.title = '编辑文章类型';
  modal.params = { operationType: 'update', id: typeId };
  modal.component = shallowRef(articleTypeEdit);
};

const deleteBtnOkClick = (typeId) => {
  articleTypesDelete(typeId)
    .then(response => {
      if (response.data) {
        ElMessage.success('删除成功');
        getPageList();
      } else {
        ElMessage.error(response.message || '删除失败');
      }
    })
    .catch(error => {
      console.error('删除操作失败', error);
      ElMessage.error('删除操作失败');
    });
};

const detailBtnClick = (typeId) => {
  detailModal.visible = true;
  detailModal.title = '文章类型详情';
  detailModal.params = { id: typeId };
  detailModal.component = shallowRef(articleTypeDetail);
};

const onOk = () => {
  modal.visible = false;
  getPageList();
};

const onCancel = () => {
  modal.visible = false;
};

const detailOnCancel = () => {
  detailModal.visible = false;
};

onMounted(() => {
  getPageList();
});
</script>

<style scoped>
.article-type-mgt {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  overflow: hidden;
}

/* 头部卡片 */
.header-card {
  margin-bottom: 0 !important;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
  color: #1f2937;
  font-size: 20px;
  font-weight: 600;
}

.page-title svg {
  color: #2563eb;
  flex-shrink: 0;
}

.add-btn {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
}

/* 内容卡片 */
.content-card {
  flex: 1;
  overflow: hidden;
  border-radius: 12px !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05) !important;
}

.table-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 搜索区域 */
.search-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
}

.search-wrapper {
  flex: 1;
  transition: all 0.3s ease;
}

.search-wrapper:not(.expanded) {
  max-height: 0;
  overflow: hidden;
  margin-bottom: 0 !important;
}

.search-form {
  transition: all 0.3s ease;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px !important;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05) !important;
}

.actions {
  display: flex;
  gap: 8px;
}

.actions .el-button {
  background: #f9fafb !important;
  border: 1px solid #e5e7eb !important;
  color: #6b7280 !important;
  transition: all 0.3s ease;
}

.actions .el-button:hover {
  background: #2563eb !important;
  border-color: #2563eb !important;
  color: #fff !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

/* 表格区域 */
.table-wrapper {
  flex: 1;
  overflow: auto;
}

.modern-table {
  border-radius: 8px;
  overflow: hidden;
}

.modern-table :deep(.el-table__header th) {
  padding: 14px 16px !important;
  font-size: 14px;
}

.modern-table :deep(.el-table__body td) {
  padding: 12px 16px !important;
  font-size: 14px;
  color: #374151;
}

.type-name {
  font-weight: 500;
  color: #1f2937;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  padding: 8px 14px !important;
  border-radius: 6px !important;
  transition: all 0.2s ease;
}

.action-btn span {
  margin-left: 4px;
  font-weight: 500;
}

.detail-btn:hover {
  background: linear-gradient(135deg, #3b82f6, #2563eb) !important;
  border-color: #2563eb !important;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.edit-btn:hover {
  background: linear-gradient(135deg, #10b981, #059669) !important;
  border-color: #059669 !important;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.delete-btn:hover {
  background: linear-gradient(135deg, #ef4444, #dc2626) !important;
  border-color: #dc2626 !important;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

/* 固定列样式 */
.action-column {
  position: sticky !important;
  right: 0;
  background: #fff;
  z-index: 10;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.08);
}

/* 分页 */
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.modern-pagination :deep(.el-pagination) {
  font-size: 14px;
}

.modern-pagination :deep(.el-pagination__total) {
  color: #6b7280;
  margin-right: 16px;
}

.modern-pagination :deep(.el-pagination__editor) {
  width: 60px !important;
}

.modern-pagination :deep(.btn-prev),
.modern-pagination :deep(.btn-next),
.modern-pagination :deep(.el-pager li) {
  background: #f9fafb !important;
  border: 1px solid #e5e7eb !important;
  color: #374151 !important;
  transition: all 0.2s ease;
}

.modern-pagination :deep(.btn-prev:hover),
.modern-pagination :deep(.btn-next:hover),
.modern-pagination :deep(.el-pager li:hover) {
  background: #2563eb !important;
  border-color: #2563eb !important;
  color: #fff !important;
}

.modern-pagination :deep(.el-pager li.is-active) {
  background: #2563eb !important;
  border-color: #2563eb !important;
  color: #fff !important;
  font-weight: 600;
}

/* 弹窗样式 */
:deep(.modern-dialog .el-dialog__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
}

:deep(.modern-dialog .el-dialog__body) {
  padding: 20px;
}

:deep(.modern-dialog .el-dialog__footer) {
  padding: 12px 20px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.modern-dialog .el-button) {
  padding: 8px 16px;
  border-radius: 6px;
}

:deep(.modern-dialog .el-button--primary) {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}
</style>