<script>
export default {
  onLaunch() {
    console.log('飞花令 App Launch')
  }
}
</script>

<style>
page {
  background-color: #f5f0e8;
  color: #2c2c2c;
  font-family: "STKaiti", "KaiTi", "楷体", serif;
}

.ink-card {
  background: rgba(255, 255, 255, 0.85);
  border-radius: 16rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid rgba(0, 0, 0, 0.06);
  padding: 32rpx;
  margin: 24rpx;
}

.ink-title {
  font-size: 44rpx;
  font-weight: bold;
  text-align: center;
  color: #1a1a1a;
  letter-spacing: 8rpx;
}

.ink-subtitle {
  font-size: 28rpx;
  color: #888;
  text-align: center;
  margin-top: 12rpx;
}

.pinyin {
  font-size: 22rpx;
  color: #999;
  letter-spacing: 2rpx;
}

.btn-primary {
  background: linear-gradient(135deg, #2c2c2c, #4a4a4a);
  color: #f5f0e8;
  border-radius: 48rpx;
  font-size: 32rpx;
  letter-spacing: 4rpx;
  margin: 24rpx;
}

.btn-secondary {
  background: #fff;
  color: #2c2c2c;
  border: 1rpx solid #ccc;
  border-radius: 48rpx;
  margin: 16rpx 24rpx;
  font-size: 32rpx;
}
</style>
