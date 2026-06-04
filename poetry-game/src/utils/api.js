async function request(url, method = 'GET', data = null) {
  const options = {
    method,
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include'
  }
  if (data != null) {
    options.body = JSON.stringify(data)
  }

  const res = await fetch(url, options)
  const json = await res.json()
  if (json && json.code === 200) {
    return json.data
  }
  throw new Error(json?.message || '请求失败')
}

export const getLevels = () => request('/api/app/levels')

export const getQuestions = (levelId, count = 10) =>
  request(`/api/app/levels/${levelId}/questions?count=${count}`)

export const checkAnswer = (questionId, answer) =>
  request('/api/app/questions/check', 'POST', { questionId, answer })

export const getPoems = (page = 1, size = 10, grade, keywordId) => {
  let url = `/api/app/poems?page=${page}&size=${size}`
  if (grade) url += `&grade=${grade}`
  if (keywordId) url += `&keywordId=${keywordId}`
  return request(url)
}

export const getPoem = (id) => request(`/api/app/poems/${id}`)

export const getKeywords = () => request('/api/app/keywords')
