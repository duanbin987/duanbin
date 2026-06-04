import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Game from '../views/Game.vue'
import Result from '../views/Result.vue'
import Library from '../views/Library.vue'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/game/:levelId', name: 'game', component: Game, props: true },
  { path: '/result', name: 'result', component: Result },
  { path: '/library', name: 'library', component: Library }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  if (to.name === 'game' && to.query.levelName) {
    document.title = `${decodeURIComponent(to.query.levelName)} · 飞花令`
  } else if (to.name === 'library') {
    document.title = '诗词库 · 飞花令'
  } else {
    document.title = '飞花令 · 诗词闯关'
  }
})

export default router
