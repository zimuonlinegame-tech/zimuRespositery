<template>
  <div class="card" style="max-width: 420px; margin: 0 auto; padding: 2rem; box-sizing: border-box;">
    <h2>登录</h2>
    <p>使用管理员账号 admin / Admin@123 体验全部功能。</p>
    <div v-if="error" class="alert alert-error">{{ error }}</div>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="username">用户名</label>
        <input id="username" v-model="form.username" type="text" required placeholder="请输入用户名" />
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input id="password" v-model="form.password" type="password" required placeholder="请输入密码" />
      </div>
      <button class="primary-btn" type="submit" :disabled="authStore.loading">
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
