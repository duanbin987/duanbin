<template>
  <view class="library">
    <view class="filters ink-card">
      <view class="filter-label">关键字</view>
      <view class="tags">
        <text
          v-for="item in keywords"
          :key="item.id"
          class="tag"
          :class="{ active: selectedKeyword === item.id }"
          @tap="onKeywordTap(item.id)"
        >{{ item.charValue }}</text>
      </view>
      <view class="filter-label">年级</view>
      <view class="tags">
        <text
          v-for="g in grades"
          :key="g"
          class="tag"
          :class="{ active: selectedGrade === g }"
          @tap="onGradeTap(g)"
        >{{ g }}年级</text>
      </view>
    </view>

    <view
      v-for="item in poems"
      :key="item.id"
      class="poem-item ink-card"
      @tap="showDetail(item.id)"
    >
      <view class="poem-title">{{ item.title }}</view>
      <view class="poem-meta">{{ item.dynasty }} · {{ item.author }} · {{ item.grade }}年级</view>
      <view class="poem-summary">{{ item.contentSummary }}</view>
    </view>

    <view v-if="detail" class="modal-mask" @tap="closeDetail">
      <view class="modal ink-card" @tap.stop>
        <view class="poem-title">{{ detail.title }}</view>
        <view class="poem-meta">{{ detail.dynasty }} · {{ detail.author }}</view>
        <view v-for="line in detail.lines" :key="line.id" class="line-block">
          <view class="line-text">{{ line.content }}</view>
          <view class="pinyin">{{ line.pinyin }}</view>
        </view>
        <button class="btn-primary" @tap="closeDetail">关闭</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPoems, getPoem, getKeywords } from '../../utils/api.js'

const poems = ref([])
const keywords = ref([])
const selectedKeyword = ref(null)
const selectedGrade = ref(null)
const page = ref(1)
const detail = ref(null)
const grades = [1, 2, 3, 4, 5, 6]

onLoad(() => {
  loadKeywords()
  loadPoems()
})

async function loadKeywords() {
  try {
    keywords.value = await getKeywords()
  } catch (e) {
    /* ignore */
  }
}

async function loadPoems() {
  try {
    const res = await getPoems(page.value, 10, selectedGrade.value, selectedKeyword.value)
    poems.value = res.records
  } catch (e) {
    uni.showToast({ title: String(e), icon: 'none' })
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
    uni.showToast({ title: String(err), icon: 'none' })
  }
}

function closeDetail() {
  detail.value = null
}
</script>

<style scoped>
.library {
  padding-bottom: 40rpx;
}
.filter-label {
  font-size: 26rpx;
  color: #888;
  margin-bottom: 12rpx;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 20rpx;
}
.tag {
  padding: 8rpx 24rpx;
  border: 1rpx solid #ccc;
  border-radius: 24rpx;
  font-size: 26rpx;
}
.tag.active {
  background: #2c2c2c;
  color: #f5f0e8;
  border-color: #2c2c2c;
}
.poem-item {
  margin-bottom: 16rpx;
}
.poem-title {
  font-size: 34rpx;
  font-weight: bold;
}
.poem-meta {
  font-size: 24rpx;
  color: #888;
  margin: 8rpx 0;
}
.poem-summary {
  font-size: 26rpx;
  color: #666;
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
  width: 85%;
  max-height: 80vh;
  overflow-y: auto;
}
.line-block {
  margin: 16rpx 0;
  text-align: center;
}
.line-text {
  font-size: 34rpx;
  letter-spacing: 4rpx;
}
</style>
