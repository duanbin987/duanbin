import { baseUrl } from '../config/env.js'

function request(url, method = 'GET', data = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: baseUrl + url,
      method,
      data,
      header: { 'Content-Type': 'application/json' },
      success(res) {
        if (res.data && res.data.code === 200) {
          resolve(res.data.data)
        } else {
          reject(res.data?.message || '请求失败')
        }
      },
      fail: reject
    })
  })
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
