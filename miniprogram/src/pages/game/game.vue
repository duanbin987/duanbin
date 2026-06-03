<template>
  <view v-if="!loading && current" class="game">
    <view class="status-bar">
      <view class="hearts">
        <text v-for="n in hearts" :key="'h' + n" class="heart">♥</text>
        <text v-for="n in emptyHearts" :key="'e' + n" class="heart empty">♡</text>
      </view>
      <view class="timer">{{ timeLeft }}s</view>
      <view class="progress">{{ currentIndex + 1 }}/{{ questions.length }}</view>
    </view>

    <view class="question ink-card">
      <view class="keyword-tag">飞花字：{{ current.keyword }}</view>
      <view class="poem-info">{{ current.poemTitle }} · {{ current.author }}</view>

      <view v-if="current.type === 'FIND_LINE'" class="q-text">
        哪一句含有「{{ current.keyword }}」？
      </view>
      <view v-else-if="current.type === 'FILL_CHAR'" class="q-text">请填写缺失的字：</view>
      <view v-else-if="current.type === 'MATCH_LINE'" class="q-text">
        选出另一句也含「{{ current.keyword }}」的诗：
      </view>

      <view v-if="current.type === 'FILL_CHAR'" class="fill-area">
        <view class="line-text">{{ current.displayLine }}</view>
        <view class="pinyin">{{ current.displayPinyin }}</view>
        <input
          class="fill-input"
          maxlength="1"
          placeholder="填字"
          v-model="fillAnswer"
        />
        <button class="btn-primary" @tap="onFillSubmit">确认</button>
      </view>

      <view v-if="current.type === 'MATCH_LINE'" class="ref-line">
        <view class="line-text">{{ current.displayLine }}</view>
        <view class="pinyin">{{ current.displayPinyin }}</view>
      </view>

      <view
        v-if="current.type === 'FIND_LINE' || current.type === 'MATCH_LINE'"
        class="options"
      >
        <view
          v-for="item in current.lines"
          :key="item.id"
          class="option ink-card"
          @tap="onSelectLine(item.id)"
        >
          <view class="line-text">{{ item.content }}</view>
          <view class="pinyin">{{ item.pinyin }}</view>
          <view v-if="current.type === 'MATCH_LINE'" class="poem-from">
            {{ item.poemTitle }}
          </view>
        </view>
      </view>
    </view>
  </view>
  <view v-else class="loading">加载题目中...</view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { getQuestions, checkAnswer } from '../../utils/api.js'
import { saveProgress, saveScore } from '../../utils/storage.js'
import { calcScore, calcStars } from '../../utils/score.js'

const levelId = ref(null)
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

onLoad((options) => {
  levelId.value = options.levelId
  levelName.value = decodeURIComponent(options.levelName || '')
  uni.setNavigationBarTitle({ title: levelName.value })
  loadQuestions()
})

onUnload(() => {
  clearTimer()
})

async function loadQuestions() {
  try {
    questions.value = await getQuestions(levelId.value, 10)
    current.value = questions.value[0]
    loading.value = false
    startTimer()
  } catch (e) {
    uni.showToast({ title: String(e), icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1500)
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
      uni.showToast({ title: '答对了!', icon: 'success', duration: 800 })
      setTimeout(() => nextQuestion(), 900)
    } else {
      handleWrong(res.correctAnswer)
    }
  } catch (e) {
    uni.showToast({ title: String(e), icon: 'none' })
    nextQuestion()
  }
}

function handleWrong(correctAnswer) {
  hearts.value -= 1
  uni.showToast({
    title: correctAnswer ? `正确答案：${correctAnswer}` : '时间到!',
    icon: 'none'
  })
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
    uni.showToast({ title: '请输入答案', icon: 'none' })
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
    levelId: levelId.value,
    levelName: levelName.value,
    correct: correct.value,
    total,
    score,
    stars,
    hearts: hearts.value
  })
  const progress = uni.getStorageSync('feihua_progress') || {}
  const prev = progress[levelId.value]?.bestScore || 0
  saveProgress(levelId.value, { bestScore: Math.max(prev, score), stars })
  uni.redirectTo({
    url: `/pages/result/result?correct=${correct.value}&total=${total}&score=${score}&stars=${stars}&hearts=${hearts.value}&levelName=${encodeURIComponent(levelName.value)}`
  })
}
</script>

<style scoped>
.game {
  padding: 16rpx;
}
.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 24rpx;
}
.hearts {
  font-size: 36rpx;
}
.heart {
  color: #c0392b;
}
.heart.empty {
  color: #ddd;
}
.timer {
  font-size: 36rpx;
  font-weight: bold;
  color: #c0392b;
}
.progress {
  font-size: 28rpx;
  color: #888;
}
.keyword-tag {
  display: inline-block;
  background: #2c2c2c;
  color: #f5f0e8;
  padding: 8rpx 24rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  margin-bottom: 16rpx;
}
.poem-info {
  font-size: 26rpx;
  color: #888;
  margin-bottom: 24rpx;
}
.q-text {
  font-size: 32rpx;
  margin-bottom: 24rpx;
}
.line-text {
  font-size: 36rpx;
  line-height: 1.8;
  letter-spacing: 4rpx;
}
.fill-input {
  border-bottom: 2rpx solid #2c2c2c;
  text-align: center;
  font-size: 48rpx;
  width: 80rpx;
  margin: 24rpx auto;
}
.option {
  margin-bottom: 16rpx;
}
.ref-line {
  margin-bottom: 24rpx;
  padding: 16rpx;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 8rpx;
}
.poem-from {
  font-size: 22rpx;
  color: #aaa;
  margin-top: 8rpx;
}
.loading {
  text-align: center;
  padding: 100rpx;
  color: #888;
}
</style>
