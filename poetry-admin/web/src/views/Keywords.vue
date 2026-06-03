<template>
  <div>
    <div class="toolbar">
      <h2>关键字管理</h2>
      <el-button type="primary" @click="openDialog()">新增关键字</el-button>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="charValue" label="字" width="80">
        <template #default="{ row }"><span class="char">{{ row.charValue }}</span></template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑' : '新增'" width="400px">
      <el-form :model="form" label-width="60px">
        <el-form-item label="字"><el-input v-model="form.charValue" maxlength="1" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = reactive({ id: null, charValue: '', description: '' })

async function loadData() {
  const res = await request.get('/admin/keywords')
  list.value = res.data
}

function openDialog(row) {
  if (row) Object.assign(form, { ...row })
  else Object.assign(form, { id: null, charValue: '', description: '' })
  visible.value = true
}

async function handleSave() {
  if (form.id) await request.put(`/admin/keywords/${form.id}`, form)
  else await request.post('/admin/keywords', form)
  ElMessage.success('保存成功')
  visible.value = false
  loadData()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？')
  await request.delete(`/admin/keywords/${id}`)
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.char { font-size: 24px; font-weight: bold; color: #e94560; }
</style>
