const PROGRESS_KEY = 'feihua_progress'
const SCORE_KEY = 'feihua_scores'

export function getProgress() {
  return uni.getStorageSync(PROGRESS_KEY) || {}
}

export function saveProgress(levelId, data) {
  const all = getProgress()
  all[levelId] = { ...all[levelId], ...data, updatedAt: Date.now() }
  uni.setStorageSync(PROGRESS_KEY, all)
}

export function getScores() {
  return uni.getStorageSync(SCORE_KEY) || []
}

export function saveScore(record) {
  const scores = getScores()
  scores.unshift({ ...record, time: Date.now() })
  uni.setStorageSync(SCORE_KEY, scores.slice(0, 50))
}
