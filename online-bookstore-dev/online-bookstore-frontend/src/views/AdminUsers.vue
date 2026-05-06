<template>
  <div class="admin-users">
    <h2>User Management</h2>
    <table class="users-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Username</th>
          <th>Email</th>
          <th>Role</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.role }}</td>
          <td>
            <button @click="editUser(user)">Edit</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Edit Modal -->
    <div v-if="editingUser" class="modal">
      <div class="modal-content">
        <h3>Edit User: {{ editingUser.username }}</h3>
        <form @submit.prevent="handleUpdate">
          <div class="form-group">
            <label>New Password (leave blank to keep)</label>
            <input v-model="form.password" type="password" />
          </div>
          <div class="form-group">
            <label>Email</label>
            <input v-model="form.email" type="email" />
          </div>
          <div class="modal-actions">
            <button type="submit">Update</button>
            <button type="button" @click="editingUser = null" class="cancel-btn">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import api from '../services/api';

const users = ref([]);
const editingUser = ref(null);
const form = reactive({
  password: '',
  email: ''
});

const fetchUsers = async () => {
  try {
    const res = await api.get('/admin/users');
    if (res.code === 200) {
      users.value = res.data || [];
    }
  } catch (e) {
    console.error(e);
  }
};

const editUser = (user) => {
  editingUser.value = user;
  form.email = user.email;
  form.password = '';
};

const handleUpdate = async () => {
  try {
    const payload = {
      id: editingUser.value.id,
      email: form.email
    };
    if (form.password) {
      payload.password = form.password;
    }

    const res = await api.post('/admin/users/update', payload);
    if (res.code === 200) {
      alert('User updated');
      editingUser.value = null;
      fetchUsers();
    }
  } catch (e) {
    // handled
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.users-table {
  width: 100%;
  border-collapse: collapse;
}
.users-table th, .users-table td {
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
  text-align: left;
}
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 400px;
}
.form-group {
  margin-bottom: 1rem;
}
.form-group label {
  display: block;
  margin-bottom: 0.5rem;
}
.form-group input {
  width: 100%;
  padding: 0.5rem;
}
.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}
.cancel-btn {
  background-color: #ccc;
}
</style>
