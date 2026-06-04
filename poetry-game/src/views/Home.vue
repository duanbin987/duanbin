<template>
  <div class="page-container">
    <div class="banner ink-card">
      <div class="ink-title">飞花令</div>
      <div class="ink-subtitle">诗词闯关 · 趣味学古诗</div>
    </div>

    <div class="section-title">选择关卡</div>
    <div
      v-for="item in levels"
      :key="item.id"
      class="level-card ink-card"
      @click="startGame(item)"
    >
      <div class="level-name">{{ item.name }}</div>
      <div class="level-info">
        <span>关键字：{{ item.keywordChar }}</span>
        <span class="diff">难度 {{ item.difficulty }}</span>
      </div>
      <div class="level-desc">{{ item.description }}</div>
      <div v-if="progress[item.id]" class="best-score">
        最佳：{{ progress[item.id].bestScore || 0 }}分
      </div>
    </div>

    <button class="btn-primary" @click="goLibrary">诗词库</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getLevels } from '../utils/api.js'
import { getProgress } from '../utils/storage.js'
import { toast } from '../utils/toast.js'

const router = useRouter()
const levels = ref([])
const progress = ref({})

async function loadLevels() {
  try {
    levels.value = await getLevels()
    progress.value = getProgress()
  } catch (e) {
    toast(String(e.message || e))
  }
}

function startGame(item) {
  router.push({
    name: 'game',
    params: { levelId: item.id },
    query: { levelName: encodeURIComponent(item.name) }
  })
}

function goLibrary() {
  router.push({ name: 'library' })
}

onMounted(loadLevels)
</script>

<style scoped>
.banner {
  margin-top: 24px;
  padding: 32px 20px;
  background: linear-gradient(180deg, #fff 0%, #f5f0e8 100%);
}

.level-card {
  position: relative;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
}

.level-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.level-name {
  font-size: 20px;
  font-weight: bold;
}

.level-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 14px;
  color: #888;
}

.diff {
  color: #c0392b;
}

.level-desc {
  font-size: 13px;
  color: #aaa;
  margin-top: 6px;
}

.best-score {
  position: absolute;
  right: 20px;
  top: 20px;
  font-size: 13px;
  color: #c0392b;
}
</style>
