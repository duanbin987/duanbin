const PROGRESS_KEY = 'feihua_progress'
const SCORE_KEY = 'feihua_scores'

export function getProgress() {
  try {
    return JSON.parse(localStorage.getItem(PROGRESS_KEY) || '{}')
  } catch {
    return {}
  }
}

export function saveProgress(levelId, data) {
  const all = getProgress()
  all[levelId] = { ...all[levelId], ...data, updatedAt: Date.now() }
  localStorage.setItem(PROGRESS_KEY, JSON.stringify(all))
}

export function getScores() {
  try {
    return JSON.parse(localStorage.getItem(SCORE_KEY) || '[]')
  } catch {
    return []
  }
}

export function saveScore(record) {
  const scores = getScores()
  scores.unshift({ ...record, time: Date.now() })
  localStorage.setItem(SCORE_KEY, JSON.stringify(scores.slice(0, 50)))
}
