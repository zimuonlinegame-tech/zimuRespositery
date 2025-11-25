<template>
  <div class="card" style="max-width: 480px; margin: 0 auto; padding: 2.5rem; box-sizing: border-box; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.35);">
    <div style="text-align: center; margin-bottom: 2rem;">
      <h2 style="margin-bottom: 0.5rem; font-size: 1.8rem; color: var(--text-strong);">登录</h2>
      <p style="color: var(--muted); font-size: 0.95rem;">使用管理员账号 admin / Admin@123 体验全部功能。</p>
    </div>
    <div v-if="error" class="alert alert-error">{{ error }}</div>
    <form @submit.prevent="handleSubmit" style="display: flex; flex-direction: column; gap: 1.5rem;">
      <div class="form-group" style="display: flex; flex-direction: column; gap: 0.5rem;">
        <label for="username" style="font-weight: 600; font-size: 0.95rem; color: var(--text-strong);">用户名</label>
        <input id="username" v-model="form.username" type="text" required placeholder="请输入用户名" />
      </div>
      <div class="form-group" style="display: flex; flex-direction: column; gap: 0.5rem;">
        <label for="password" style="font-weight: 600; font-size: 0.95rem; color: var(--text-strong);">密码</label>
        <input id="password" v-model="form.password" type="password" required placeholder="请输入密码" />
      </div>
      <button class="primary-btn" type="submit" :disabled="authStore.loading" style="margin-top: 0.5rem; padding: 0.8rem 1.5rem; font-size: 1rem;">
        {{ authStore.loading ? '登录中...' : '登录' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();
const error = ref('');
const form = reactive({
  username: '',
  password: ''
});

const handleSubmit = async () => {
  error.value = '';
  try {
    await authStore.login({ ...form });
    const redirect = route.query.redirect || '/users';
    router.push(redirect);
  } catch (e) {
    error.value = e.response?.data?.message || '登录失败，请稍后重试';
  }
};
</script>
