<template>
  <div>
    <div class="card">
      <div style="display: flex; justify-content: space-between; align-items: center; gap: 1rem; flex-wrap: wrap;">
        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
          <input v-model="keyword" placeholder="搜索用户名或邮箱" style="padding: 0.5rem; border-radius: 6px; border: 1px solid #d1d5db;" />
          <button class="secondary-btn" @click="handleSearch">搜索</button>
        </div>
        <div style="display: flex; gap: 0.5rem;">
          <button class="secondary-btn" @click="fetchUsers">刷新</button>
          <button class="primary-btn" @click="startCreate">新增用户</button>
        </div>
      </div>
      <div v-if="error" class="alert alert-error" style="margin-top: 1rem;">{{ error }}</div>
      <div v-if="info" class="alert alert-info" style="margin-top: 1rem;">{{ info }}</div>
      <table class="table">
        <thead>
          <tr>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>权限</th>
            <th>启用</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>
              <span v-for="role in user.roles" :key="role" class="badge">{{ role }}</span>
            </td>
            <td>
              <span v-for="permission in user.permissions" :key="permission" class="badge" style="background-color:#fef3c7;color:#92400e;">{{ permission }}</span>
            </td>
            <td style="text-align: center;">
              <span class="badge" :style="{ backgroundColor: user.enabled ? '#dcfce7' : '#fee2e2', color: user.enabled ? '#15803d' : '#b91c1c' }">
                {{ user.enabled ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td style="white-space: nowrap;">
              <button class="secondary-btn" @click="editUser(user)">编辑</button>
              <button class="secondary-btn" style="margin-left: 0.5rem; border-color:#dc2626;color:#dc2626;" @click="removeUser(user)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div style="display:flex; justify-content: space-between; align-items: center; margin-top: 1rem; flex-wrap: wrap; gap: 0.5rem;">
        <div>共 {{ totalElements }} 条记录</div>
        <div style="display: flex; gap: 0.5rem; align-items: center;">
          <button class="secondary-btn" :disabled="page === 0" @click="changePage(page - 1)">上一页</button>
          <span>第 {{ page + 1 }} / {{ Math.max(totalPages, 1) }} 页</span>
          <button class="secondary-btn" :disabled="page + 1 >= totalPages" @click="changePage(page + 1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑用户模态框 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editing ? '编辑用户' : '创建新用户' }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div v-if="error" class="alert alert-error" style="margin-bottom: 1rem;">{{ error }}</div>
          <form @submit.prevent="submitForm">
            <div class="form-group">
              <label style="display: inline-block; width: 100px;">用户名</label>
              <input v-model="form.username" :disabled="editing" required placeholder="唯一用户名" style="width: 250px;" />
            </div>
            <div class="form-group">
              <label style="display: inline-block; width: 100px;">密码 {{ editing ? '(留空表示不修改)' : '' }}</label>
              <input v-model="form.password" type="password" :required="!editing" minlength="6" placeholder="至少 6 位" style="width: 250px;" />
            </div>
            <div class="form-group">
              <label style="display: inline-block; width: 100px;">邮箱</label>
              <input v-model="form.email" type="email" placeholder="用户邮箱" style="width: 250px;" />
            </div>
            <div class="form-group">
              <label>角色</label>
              <div style="display:flex; gap: 1rem; flex-wrap: wrap;">
                <label v-for="role in availableRoles" :key="role.name" style="display:flex; align-items:center; gap:0.4rem;">
                  <input type="checkbox" :value="role.name" v-model="form.roles" />
                  {{ role.displayName }}
                </label>
              </div>
            </div>
            <div class="form-group" style="display: flex; align-items: center; gap: 0.5rem;">
              <label style="width: 100px; margin: 0;">启用状态</label>
              <input type="checkbox" v-model="form.enabled" style="margin: 0;" />
            </div>
            <div style="display:flex; gap:0.5rem; margin-top: 1.5rem;">
              <button class="primary-btn" type="submit">{{ editing ? '保存修改' : '创建用户' }}</button>
              <button type="button" class="secondary-btn" @click="closeModal">取消</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const users = ref([]);
const page = ref(0);
const size = ref(10);
const totalPages = ref(0);
const totalElements = ref(0);
const keyword = ref('');
const error = ref('');
const info = ref('');
const editing = ref(false);
const editingId = ref(null);
const showModal = ref(false);
const availableRoles = ref([]);

const form = reactive({
  username: '',
  email: '',
  password: '',
  enabled: true,
  roles: ['USER']
});

const resetForm = () => {
  form.username = '';
  form.email = '';
  form.password = '';
  form.enabled = true;
  form.roles = ['USER'];
  editing.value = false;
  editingId.value = null;
};

const closeModal = () => {
  showModal.value = false;
  resetForm();
};

const startCreate = () => {
  resetForm();
  info.value = '';
  showModal.value = true;
};

const fetchRoles = async () => {
  try {
    const response = await api.get('/api/roles');
    availableRoles.value = response.data.data;
  } catch (e) {
    console.error('加载角色失败:', e);
    // 如果加载失败，使用默认角色
    availableRoles.value = [
      { name: 'ADMIN', displayName: '系统管理员' },
      { name: 'USER', displayName: '普通用户' }
    ];
  }
};

const fetchUsers = async () => {
  try {
    error.value = '';
    const response = await api.get('/api/users', {
      params: { page: page.value, size: size.value, keyword: keyword.value || undefined }
    });
    const payload = response.data.data;
    users.value = payload.items;
    totalPages.value = payload.totalPages;
    totalElements.value = payload.totalElements;
  } catch (e) {
    if (e.response?.status === 401) {
      authStore.logout();
    }
    error.value = e.response?.data?.message || '加载用户失败';
  }
};

const handleSearch = () => {
  page.value = 0;
  fetchUsers();
};

const changePage = (p) => {
  if (p < 0 || p >= totalPages.value) return;
  page.value = p;
  fetchUsers();
};

const editUser = (user) => {
  editing.value = true;
  editingId.value = user.id;
  form.username = user.username;
  form.email = user.email;
  form.password = '';
  form.enabled = user.enabled;
  form.roles = [...user.roles];
  info.value = `正在编辑用户 ${user.username}`;
  showModal.value = true;
};

const buildPayload = () => {
  const base = {
    email: form.email,
    enabled: form.enabled,
    roles: form.roles
  };
  if (!form.roles.length) {
    base.roles = ['USER'];
  }
  if (form.password) {
    base.password = form.password;
  }
  return base;
};

const submitForm = async () => {
  try {
    error.value = '';
    if (editing.value) {
      await api.put(`/api/users/${editingId.value}`, buildPayload());
      info.value = '用户更新成功';
      closeModal();
      fetchUsers();
    } else {
      const payload = { ...buildPayload(), username: form.username, password: form.password };
      if (!payload.password) {
        error.value = '请填写密码';
        return;
      }
      await api.post('/api/users', payload);
      info.value = '用户创建成功';
      closeModal();
      fetchUsers();
    }
  } catch (e) {
    error.value = e.response?.data?.message || '操作失败';
  }
};

const removeUser = async (user) => {
  if (!window.confirm(`确定删除用户 ${user.username} ?`)) {
    return;
  }
  try {
    await api.delete(`/api/users/${user.id}`);
    info.value = '用户已删除';
    fetchUsers();
  } catch (e) {
    error.value = e.response?.data?.message || '删除失败';
  }
};

const formatDate = (value) => {
  if (!value) return '';
  return new Date(value).toLocaleString();
};

onMounted(() => {
  fetchRoles();
  if (authStore.isAuthenticated) {
    fetchUsers();
  }
});
</script>
