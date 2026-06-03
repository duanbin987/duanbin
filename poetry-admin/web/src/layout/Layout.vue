<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">飞花令后台</div>
      <el-menu :default-active="$route.path" router background-color="#1a1a2e" text-color="#ccc" active-text-color="#e94560">
        <el-menu-item index="/dashboard"><el-icon><DataAnalysis /></el-icon>数据概览</el-menu-item>
        <el-menu-item index="/poems"><el-icon><Reading /></el-icon>诗词管理</el-menu-item>
        <el-menu-item index="/keywords"><el-icon><CollectionTag /></el-icon>关键字管理</el-menu-item>
        <el-menu-item index="/levels"><el-icon><Trophy /></el-icon>关卡管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>欢迎，{{ username }}</span>
        <el-button type="danger" link @click="handleLogout">退出</el-button>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = computed(() => userStore.username)

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.aside { background: #1a1a2e; }
.logo { color: #e94560; font-size: 20px; font-weight: bold; text-align: center; padding: 20px 0; border-bottom: 1px solid #333; }
.header { display: flex; justify-content: space-between; align-items: center; background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,.08); }
</style>
