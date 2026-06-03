<template>
  <div>
    <div class="toolbar">
      <h2>关卡管理</h2>
      <el-button type="primary" @click="openDialog()">新增关卡</el-button>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="keywordChar" label="关键字" width="80" />
      <el-table-column prop="difficulty" label="难度" width="80" />
      <el-table-column prop="poemCount" label="诗词数" width="80" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑关卡' : '新增关卡'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="关键字">
          <el-select v-model="form.keywordId" placeholder="选择关键字">
            <el-option v-for="k in keywords" :key="k.id" :label="k.charValue" :value="k.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度"><el-input-number v-model="form.difficulty" :min="1" :max="3" /></el-form-item>
        <el-form-item label="诗词数"><el-input-number v-model="form.poemCount" :min="1" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
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
const keywords = ref([])
const visible = ref(false)
const form = reactive({ id: null, name: '', keywordId: null, difficulty: 1, poemCount: 5, description: '', sortOrder: 0 })

async function loadData() {
  const [levelsRes, kwRes] = await Promise.all([
    request.get('/admin/levels'),
    request.get('/admin/keywords')
  ])
  list.value = levelsRes.data
  keywords.value = kwRes.data
}

function openDialog(row) {
  if (row) Object.assign(form, { ...row })
  else Object.assign(form, { id: null, name: '', keywordId: keywords.value[0]?.id, difficulty: 1, poemCount: 5, description: '', sortOrder: 0 })
  visible.value = true
}

async function handleSave() {
  if (form.id) await request.put(`/admin/levels/${form.id}`, form)
  else await request.post('/admin/levels', form)
  ElMessage.success('保存成功')
  visible.value = false
  loadData()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？')
  await request.delete(`/admin/levels/${id}`)
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
