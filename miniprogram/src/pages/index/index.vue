<template>
  <view class="container">
    <view class="banner ink-card">
      <view class="ink-title">飞花令</view>
      <view class="ink-subtitle">诗词闯关 · 趣味学古诗</view>
    </view>

    <view class="section-title">选择关卡</view>
    <view
      v-for="item in levels"
      :key="item.id"
      class="level-card ink-card"
      @tap="startGame(item)"
    >
      <view class="level-name">{{ item.name }}</view>
      <view class="level-info">
        <text>关键字：{{ item.keywordChar }}</text>
        <text class="diff">难度 {{ item.difficulty }}</text>
      </view>
      <view class="level-desc">{{ item.description }}</view>
      <view v-if="progress[item.id]" class="best-score">
        最佳：{{ progress[item.id].bestScore || 0 }}分
      </view>
    </view>

    <button class="btn-primary" @tap="goLibrary">诗词库</button>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLevels } from '../../utils/api.js'
import { getProgress } from '../../utils/storage.js'

const levels = ref([])
const progress = ref({})

async function loadLevels() {
  try {
    levels.value = await getLevels()
    progress.value = getProgress()
  } catch (e) {
    uni.showToast({ title: String(e), icon: 'none' })
  }
}

function startGame(item) {
  uni.navigateTo({
    url: `/pages/game/game?levelId=${item.id}&levelName=${encodeURIComponent(item.name)}`
  })
}

function goLibrary() {
  uni.navigateTo({ url: '/pages/library/library' })
}

onShow(() => {
  loadLevels()
})
</script>

<style scoped>
.container {
  padding-bottom: 40rpx;
}
.banner {
  margin-top: 40rpx;
  padding: 48rpx 32rpx;
  background: linear-gradient(180deg, #fff 0%, #f5f0e8 100%);
}
.section-title {
  font-size: 32rpx;
  margin: 32rpx 24rpx 16rpx;
  color: #666;
}
.level-card {
  position: relative;
}
.level-name {
  font-size: 36rpx;
  font-weight: bold;
}
.level-info {
  display: flex;
  justify-content: space-between;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: #888;
}
.diff {
  color: #c0392b;
}
.level-desc {
  font-size: 24rpx;
  color: #aaa;
  margin-top: 8rpx;
}
.best-score {
  position: absolute;
  right: 32rpx;
  top: 32rpx;
  font-size: 24rpx;
  color: #c0392b;
}
</style>
