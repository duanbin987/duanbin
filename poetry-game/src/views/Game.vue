<template>
  <div v-if="!loading && current" class="game page-container">
    <div class="status-bar">
      <div class="hearts">
        <span v-for="n in hearts" :key="'h' + n" class="heart">♥</span>
        <span v-for="n in emptyHearts" :key="'e' + n" class="heart empty">♡</span>
      </div>
      <div class="timer">{{ timeLeft }}s</div>
      <div class="progress">{{ currentIndex + 1 }}/{{ questions.length }}</div>
    </div>

    <div class="question ink-card">
      <div class="keyword-tag">飞花字：{{ current.keyword }}</div>
      <div class="poem-info">{{ current.poemTitle }} · {{ current.author }}</div>

      <div v-if="current.type === 'FIND_LINE'" class="q-text">
        哪一句含有「{{ current.keyword }}」？
      </div>
      <div v-else-if="current.type === 'FILL_CHAR'" class="q-text">请填写缺失的字：</div>
      <div v-else-if="current.type === 'MATCH_LINE'" class="q-text">
        选出另一句也含「{{ current.keyword }}」的诗：
      </div>

      <div v-if="current.type === 'FILL_CHAR'" class="fill-area">
        <div class="line-text">{{ current.displayLine }}</div>
        <div class="pinyin">{{ current.displayPinyin }}</div>
        <input
          class="fill-input"
          maxlength="1"
          placeholder="填字"
          v-model="fillAnswer"
          @keyup.enter="onFillSubmit"
        />
        <button class="btn-primary fill-btn" @click="onFillSubmit">确认</button>
      </div>

      <div v-if="current.type === 'MATCH_LINE'" class="ref-line">
        <div class="line-text">{{ current.displayLine }}</div>
        <div class="pinyin">{{ current.displayPinyin }}</div>
      </div>

      <div
        v-if="current.type === 'FIND_LINE' || current.type === 'MATCH_LINE'"
        class="options"
      >
        <div
          v-for="item in current.lines"
          :key="item.id"
          class="option ink-card"
          @click="onSelectLine(item.id)"
        >
          <div class="line-text">{{ item.content }}</div>
          <div class="pinyin">{{ item.pinyin }}</div>
          <div v-if="current.type === 'MATCH_LINE'" class="poem-from">
            {{ item.poemTitle }}
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading">加载题目中...</div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQuestions, checkAnswer } from '../utils/api.js'
import { saveProgress, saveScore, getProgress } from '../utils/storage.js'
import { calcScore, calcStars } from '../utils/score.js'
import { toast } from '../utils/toast.js'

const props = defineProps({
  levelId: { type: [String, Number], required: true }
})

const route = useRoute()
const router = useRouter()

const levelName = ref('')
const questions = ref([])
const currentIndex = ref(0)
const current = ref(null)
const hearts = ref(3)
const timeLeft = ref(15)
const correct = ref(0)
const loading = ref(true)
const fillAnswer = ref('')

let timer = null

const emptyHearts = computed(() => Math.max(0, 3 - hearts.value))

onMounted(() => {
  levelName.value = decodeURIComponent(route.query.levelName || '')
  loadQuestions()
})

onUnmounted(() => {
  clearTimer()
})

async function loadQuestions() {
  try {
    questions.value = await getQuestions(props.levelId, 10)
    current.value = questions.value[0]
    loading.value = false
    startTimer()
  } catch (e) {
    toast(String(e.message || e))
    setTimeout(() => router.push({ name: 'home' }), 1500)
  }
}

function startTimer() {
  clearTimer()
  timeLeft.value = 15
  timer = setInterval(() => {
    if (timeLeft.value <= 1) {
      handleWrong()
    } else {
      timeLeft.value -= 1
    }
  }, 1000)
}

function clearTimer() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

async function submitAnswer(answer) {
  clearTimer()
  const q = current.value
  try {
    const res = await checkAnswer(q.questionId, String(answer))
    if (res.correct) {
      correct.value += 1
      toast('答对了!', 800)
      setTimeout(() => nextQuestion(), 900)
    } else {
      handleWrong(res.correctAnswer)
    }
  } catch (e) {
    toast(String(e.message || e))
    nextQuestion()
  }
}

function handleWrong(correctAnswer) {
  clearTimer()
  hearts.value -= 1
  toast(correctAnswer ? `正确答案：${correctAnswer}` : '时间到!', 1200)
  if (hearts.value <= 0) {
    setTimeout(() => finishGame(), 1200)
  } else {
    setTimeout(() => nextQuestion(), 1200)
  }
}

function nextQuestion() {
  const next = currentIndex.value + 1
  if (next >= questions.value.length || hearts.value <= 0) {
    finishGame()
    return
  }
  currentIndex.value = next
  current.value = questions.value[next]
  fillAnswer.value = ''
  startTimer()
}

function onSelectLine(id) {
  submitAnswer(id)
}

function onFillSubmit() {
  const ans = fillAnswer.value.trim()
  if (!ans) {
    toast('请输入答案')
    return
  }
  submitAnswer(ans)
}

function finishGame() {
  clearTimer()
  const total = questions.value.length
  const score = calcScore(correct.value, total, hearts.value)
  const stars = calcStars(correct.value, total)
  saveScore({
    levelId: props.levelId,
    levelName: levelName.value,
    correct: correct.value,
    total,
    score,
    stars,
    hearts: hearts.value
  })
  const progress = getProgress()
  const prev = progress[props.levelId]?.bestScore || 0
  saveProgress(props.levelId, { bestScore: Math.max(prev, score), stars })
  router.replace({
    name: 'result',
    query: {
      correct: correct.value,
      total,
      score,
      stars,
      hearts: hearts.value,
      levelId: props.levelId,
      levelName: encodeURIComponent(levelName.value)
    }
  })
}
</script>

<style scoped>
.game {
  padding: 12px 0;
}

.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  max-width: 640px;
  margin: 0 auto;
}

.hearts {
  font-size: 22px;
}

.heart {
  color: #c0392b;
}

.heart.empty {
  color: #ddd;
}

.timer {
  font-size: 22px;
  font-weight: bold;
  color: #c0392b;
}

.progress {
  font-size: 15px;
  color: #888;
}

.question {
  margin-top: 0;
}

.keyword-tag {
  display: inline-block;
  background: #2c2c2c;
  color: #f5f0e8;
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 15px;
  margin-bottom: 12px;
}

.poem-info {
  font-size: 14px;
  color: #888;
  margin-bottom: 16px;
}

.q-text {
  font-size: 17px;
  margin-bottom: 16px;
}

.fill-input {
  display: block;
  border: none;
  border-bottom: 2px solid #2c2c2c;
  text-align: center;
  font-size: 28px;
  width: 60px;
  margin: 16px auto;
  background: transparent;
  font-family: inherit;
  outline: none;
}

.fill-btn {
  width: auto;
  min-width: 120px;
  margin-top: 8px;
}

.option {
  margin-bottom: 12px;
  cursor: pointer;
  transition: background 0.15s;
}

.option:hover {
  background: rgba(0, 0, 0, 0.03);
}

.ref-line {
  margin-bottom: 16px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 8px;
}

.poem-from {
  font-size: 12px;
  color: #aaa;
  margin-top: 6px;
}
</style>
