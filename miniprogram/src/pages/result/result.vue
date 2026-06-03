<template>
  <view class="result ink-card">
    <view class="ink-title">{{ levelName }}</view>
    <view class="stars">
      <text v-for="n in stars" :key="'s' + n" class="star">★</text>
      <text v-for="n in emptyStars" :key="'e' + n" class="star empty">☆</text>
    </view>
    <view class="score">{{ score }} 分</view>
    <view class="detail">答对 {{ correct }} / {{ total }} 题 · 剩余 {{ hearts }} 颗心</view>
    <button class="btn-primary" @tap="retry">再来一次</button>
    <button class="btn-secondary" @tap="goHome">返回首页</button>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const correct = ref(0)
const total = ref(10)
const score = ref(0)
const stars = ref(0)
const hearts = ref(0)
const levelName = ref('')

const emptyStars = computed(() => Math.max(0, 3 - stars.value))

onLoad((options) => {
  correct.value = Number(options.correct || 0)
  total.value = Number(options.total || 10)
  score.value = Number(options.score || 0)
  stars.value = Number(options.stars || 0)
  hearts.value = Number(options.hearts || 0)
  levelName.value = decodeURIComponent(options.levelName || '')
})

function goHome() {
  uni.navigateBack({ delta: 2 })
}

function retry() {
  uni.navigateBack()
}
</script>

<style scoped>
.result {
  margin-top: 80rpx;
  text-align: center;
  padding: 48rpx;
}
.stars {
  font-size: 56rpx;
  margin: 32rpx 0;
}
.star {
  color: #f1c40f;
}
.star.empty {
  color: #ddd;
}
.score {
  font-size: 64rpx;
  font-weight: bold;
  color: #c0392b;
  margin: 24rpx 0;
}
.detail {
  font-size: 28rpx;
  color: #888;
  margin-bottom: 48rpx;
}
</style>
