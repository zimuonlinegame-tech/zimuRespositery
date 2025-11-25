<template>
  <div class="app-shell">
    <template v-if="isAuthenticated">
      <header class="app-header">
        <div>
          <h1>财务管理后台</h1>
        </div>
        <div class="header-actions">
          <span v-if="currentUser" class="user-chip">{{ currentUser.username }}</span>
          <button v-if="isAuthenticated" class="secondary-btn" @click="logout">退出登录</button>
          <RouterLink v-else to="/login" class="secondary-btn">登录</RouterLink>
        </div>
      </header>

      <div class="layout">
        <aside class="sidebar">
          <nav class="menu-list">
            <RouterLink v-for="item in menus" :key="item.id" :to="item.path" class="menu-item" active-class="active">
              <span class="dot"></span>
              <span>{{ item.name }}</span>
            </RouterLink>
          </nav>
        </aside>
        <main class="app-main">
          <RouterView />
        </main>
      </div>
    </template>
    <template v-else>
      <main class="auth-container">
        <RouterView />
      </main>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { RouterLink, RouterView, useRouter } from 'vue-router';
import { useAuthStore } from './stores/auth';
import api from './services/api';

const authStore = useAuthStore();
const router = useRouter();
const isAuthenticated = computed(() => authStore.isAuthenticated);
const currentUser = computed(() => authStore.user);
const menus = ref([{ id: 1, name: '用户管理', path: '/users' }]);

const loadMenus = async () => {
  if (!authStore.isAuthenticated) return;
  try {
    const res = await api.get('/api/menus');
    menus.value = res.data.data?.length ? res.data.data : menus.value;
  } catch (e) {
    // 保留默认菜单
  }
};

const logout = () => {
  authStore.logout();
  router.push('/login');
};

onMounted(() => {
  loadMenus();
});
</script>
