<template>
  <div class="page-container">
    <div class="result ink-card">
      <div class="ink-title">{{ levelName }}</div>
      <div class="stars">
        <span v-for="n in stars" :key="'s' + n" class="star">★</span>
        <span v-for="n in emptyStars" :key="'e' + n" class="star empty">☆</span>
      </div>
      <div class="score">{{ score }} 分</div>
      <div class="detail">答对 {{ correct }} / {{ total }} 题 · 剩余 {{ hearts }} 颗心</div>
      <button class="btn-primary" @click="retry">再来一次</button>
      <button class="btn-secondary" @click="goHome">返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const correct = ref(0)
const total = ref(10)
const score = ref(0)
const stars = ref(0)
const hearts = ref(0)
const levelName = ref('')
const levelId = ref('')

const emptyStars = computed(() => Math.max(0, 3 - stars.value))

onMounted(() => {
  correct.value = Number(route.query.correct || 0)
  total.value = Number(route.query.total || 10)
  score.value = Number(route.query.score || 0)
  stars.value = Number(route.query.stars || 0)
  hearts.value = Number(route.query.hearts || 0)
  levelId.value = route.query.levelId || ''
  levelName.value = decodeURIComponent(route.query.levelName || '')
})

function goHome() {
  router.push({ name: 'home' })
}

function retry() {
  router.push({
    name: 'game',
    params: { levelId: levelId.value },
    query: { levelName: route.query.levelName }
  })
}
</script>

<style scoped>
.result {
  margin-top: 48px;
  text-align: center;
  padding: 32px;
}

.stars {
  font-size: 36px;
  margin: 24px 0;
}

.star {
  color: #f1c40f;
}

.star.empty {
  color: #ddd;
}

.score {
  font-size: 40px;
  font-weight: bold;
  color: #c0392b;
  margin: 16px 0;
}

.detail {
  font-size: 15px;
  color: #888;
  margin-bottom: 32px;
}
</style>
