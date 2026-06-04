<template>
  <div class="library page-container">
    <div class="page-header">
      <button class="back-btn" @click="goHome">← 返回</button>
      <span class="page-title">诗词库</span>
    </div>

    <div class="filters ink-card">
      <div class="filter-label">关键字</div>
      <div class="tags">
        <span
          v-for="item in keywords"
          :key="item.id"
          class="tag"
          :class="{ active: selectedKeyword === item.id }"
          @click="onKeywordTap(item.id)"
        >{{ item.charValue }}</span>
      </div>
      <div class="filter-label">年级</div>
      <div class="tags">
        <span
          v-for="g in grades"
          :key="g"
          class="tag"
          :class="{ active: selectedGrade === g }"
          @click="onGradeTap(g)"
        >{{ g }}年级</span>
      </div>
    </div>

    <div
      v-for="item in poems"
      :key="item.id"
      class="poem-item ink-card"
      @click="showDetail(item.id)"
    >
      <div class="poem-title">{{ item.title }}</div>
      <div class="poem-meta">{{ item.dynasty }} · {{ item.author }} · {{ item.grade }}年级</div>
      <div class="poem-summary">{{ item.contentSummary }}</div>
    </div>

    <div v-if="poems.length === 0 && !loading" class="empty">暂无诗词</div>

    <div v-if="detail" class="modal-mask" @click="closeDetail">
      <div class="modal ink-card" @click.stop>
        <div class="poem-title">{{ detail.title }}</div>
        <div class="poem-meta">{{ detail.dynasty }} · {{ detail.author }}</div>
        <div v-for="line in detail.lines" :key="line.id" class="line-block">
          <div class="line-text">{{ line.content }}</div>
          <div class="pinyin">{{ line.pinyin }}</div>
        </div>
        <button class="btn-primary modal-close" @click="closeDetail">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPoems, getPoem, getKeywords } from '../utils/api.js'
import { toast } from '../utils/toast.js'

const router = useRouter()
const poems = ref([])
const keywords = ref([])
const selectedKeyword = ref(null)
const selectedGrade = ref(null)
const page = ref(1)
const detail = ref(null)
const loading = ref(false)
const grades = [1, 2, 3, 4, 5, 6]

onMounted(() => {
  loadKeywords()
  loadPoems()
})

function goHome() {
  router.push({ name: 'home' })
}

async function loadKeywords() {
  try {
    keywords.value = await getKeywords()
  } catch {
    /* ignore */
  }
}

async function loadPoems() {
  loading.value = true
  try {
    const res = await getPoems(page.value, 10, selectedGrade.value, selectedKeyword.value)
    poems.value = res.records
  } catch (e) {
    toast(String(e.message || e))
  } finally {
    loading.value = false
  }
}

function onKeywordTap(id) {
  selectedKeyword.value = selectedKeyword.value === id ? null : id
  page.value = 1
  loadPoems()
}

function onGradeTap(grade) {
  selectedGrade.value = selectedGrade.value === grade ? null : grade
  page.value = 1
  loadPoems()
}

async function showDetail(id) {
  try {
    detail.value = await getPoem(id)
  } catch (err) {
    toast(String(err.message || err))
  }
}

function closeDetail() {
  detail.value = null
}
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  padding: 16px;
  max-width: 640px;
  margin: 0 auto;
  gap: 12px;
}

.back-btn {
  background: none;
  border: none;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  font-family: inherit;
  padding: 4px 8px;
}

.back-btn:hover {
  color: #2c2c2c;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
}

.filter-label {
  font-size: 14px;
  color: #888;
  margin-bottom: 8px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.tag {
  padding: 6px 14px;
  border: 1px solid #ccc;
  border-radius: 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.15s;
  user-select: none;
}

.tag:hover {
  border-color: #999;
}

.tag.active {
  background: #2c2c2c;
  color: #f5f0e8;
  border-color: #2c2c2c;
}

.poem-item {
  cursor: pointer;
  transition: transform 0.15s;
}

.poem-item:hover {
  transform: translateY(-1px);
}

.poem-title {
  font-size: 18px;
  font-weight: bold;
}

.poem-meta {
  font-size: 13px;
  color: #888;
  margin: 6px 0;
}

.poem-summary {
  font-size: 14px;
  color: #666;
}

.empty {
  text-align: center;
  color: #aaa;
  padding: 40px;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal {
  width: 90%;
  max-width: 480px;
  max-height: 80vh;
  overflow-y: auto;
  margin: 0;
}

.line-block {
  margin: 12px 0;
  text-align: center;
}

.modal-close {
  margin-top: 16px;
}
</style>
