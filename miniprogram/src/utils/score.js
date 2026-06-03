export function calcScore(correct, total, heartsLeft) {
  const base = Math.round((correct / total) * 100)
  const bonus = heartsLeft * 5
  return base + bonus
}

export function calcStars(correct, total) {
  const rate = correct / total
  if (rate >= 0.9) return 3
  if (rate >= 0.6) return 2
  if (rate >= 0.3) return 1
  return 0
}
