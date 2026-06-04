<template>
  <div class="app-shell">
    <router-view />
    <Transition name="toast">
      <div v-if="toastVisible" class="toast">{{ toastMessage }}</div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { setToastHandler } from './utils/toast.js'

const toastMessage = ref('')
const toastVisible = ref(false)
let toastTimer = null

onMounted(() => {
  setToastHandler((msg, duration = 2000) => {
    toastMessage.value = msg
    toastVisible.value = true
    if (toastTimer) clearTimeout(toastTimer)
    toastTimer = setTimeout(() => {
      toastVisible.value = false
    }, duration)
  })
})
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.toast {
  position: fixed;
  left: 50%;
  bottom: 80px;
  transform: translateX(-50%);
  background: rgba(44, 44, 44, 0.92);
  color: #f5f0e8;
  padding: 12px 24px;
  border-radius: 24px;
  font-size: 15px;
  z-index: 9999;
  max-width: 80vw;
  text-align: center;
  pointer-events: none;
}

.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(10px);
}
</style>
