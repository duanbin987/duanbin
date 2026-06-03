<template>
  <div>
    <h2 style="margin-bottom:20px">数据概览</h2>
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in cards" :key="item.label">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="value">{{ item.value }}</div>
            <div class="label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const cards = ref([
  { label: '诗词总数', value: 0 },
  { label: '诗句总数', value: 0 },
  { label: '关键字数', value: 0 },
  { label: '关卡数量', value: 0 }
])

onMounted(async () => {
  const res = await request.get('/admin/dashboard/stats')
  const d = res.data
  cards.value[0].value = d.poemCount
  cards.value[1].value = d.lineCount
  cards.value[2].value = d.keywordCount
  cards.value[3].value = d.levelCount
})
</script>

<style scoped>
.stat-card { text-align: center; padding: 20px 0; }
.value { font-size: 36px; font-weight: bold; color: #e94560; }
.label { margin-top: 8px; color: #666; }
</style>
