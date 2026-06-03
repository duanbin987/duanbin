<template>
  <div>
    <div class="toolbar">
      <h2>诗词管理</h2>
      <el-button type="primary" @click="openDialog()">新增诗词</el-button>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="author" label="作者" width="100" />
      <el-table-column prop="dynasty" label="朝代" width="80" />
      <el-table-column prop="grade" label="年级" width="70" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="10" :total="total" @current-change="loadData" style="margin-top:16px" />

    <el-dialog v-model="visible" :title="form.id ? '编辑诗词' : '新增诗词'" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="form.author" /></el-form-item>
        <el-form-item label="朝代"><el-input v-model="form.dynasty" /></el-form-item>
        <el-form-item label="年级"><el-input-number v-model="form.grade" :min="1" :max="6" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.contentSummary" type="textarea" /></el-form-item>
        <el-form-item label="诗句">
          <div v-for="(line, idx) in form.lines" :key="idx" class="line-row">
            <el-input v-model="line.content" placeholder="诗句内容" style="flex:1" />
            <el-input v-model="line.pinyin" placeholder="拼音" style="flex:1" />
            <el-button type="danger" link @click="form.lines.splice(idx, 1)">删</el-button>
          </div>
          <el-button @click="form.lines.push({ content: '', pinyin: '', lineNo: form.lines.length + 1 })">添加诗句</el-button>
        </el-form-item>
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
const page = ref(1)
const total = ref(0)
const visible = ref(false)
const form = reactive({ id: null, title: '', author: '', dynasty: '唐', grade: 1, contentSummary: '', lines: [] })

async function loadData() {
  const res = await request.get('/admin/poems', { params: { page: page.value, size: 10 } })
  list.value = res.data.records
  total.value = res.data.total
}

function openDialog(row) {
  if (row) {
    Object.assign(form, { ...row, lines: row.lines ? [...row.lines.map(l => ({ ...l }))] : [] })
  } else {
    Object.assign(form, { id: null, title: '', author: '', dynasty: '唐', grade: 1, contentSummary: '', lines: [{ content: '', pinyin: '', lineNo: 1 }] })
  }
  visible.value = true
}

async function handleSave() {
  const payload = { ...form, lines: form.lines.map((l, i) => ({ ...l, lineNo: i + 1, sortOrder: i })) }
  if (form.id) {
    await request.put(`/admin/poems/${form.id}`, payload)
  } else {
    await request.post('/admin/poems', payload)
  }
  ElMessage.success('保存成功')
  visible.value = false
  loadData()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？')
  await request.delete(`/admin/poems/${id}`)
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.line-row { display: flex; gap: 8px; margin-bottom: 8px; }
</style>
