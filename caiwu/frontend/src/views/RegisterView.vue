<template>
  <div class="card" style="max-width: 520px; margin: 0 auto;">
    <h2>注册</h2>
    <div v-if="error" class="alert alert-error">{{ error }}</div>
    <div v-if="success" class="alert alert-info">{{ success }}</div>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="username">用户名</label>
        <input id="username" v-model="form.username" required placeholder="请输入用户名" />
      </div>
      <div class="form-group">
        <label for="email">邮箱</label>
        <input id="email" type="email" v-model="form.email" required placeholder="请输入邮箱" />
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input id="password" type="password" v-model="form.password" required minlength="6" placeholder="至少 6 位" />
      </div>
      <button class="primary-btn" type="submit" :disabled="authStore.loading">
        {{ authStore.loading ? '提交中...' : '注册并登录' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const error = ref('');
const success = ref('');
const form = reactive({
  username: '',
  email: '',
  password: ''
});

const handleSubmit = async () => {
  error.value = '';
  success.value = '';
  try {
    await authStore.register({ ...form });
    success.value = '注册成功，已自动登录';
    router.push('/users');
  } catch (e) {
    error.value = e.response?.data?.message || '注册失败，请稍后重试';
  }
};
</script>
