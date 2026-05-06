<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-icon">👤</div>
        <h2>Welcome Back</h2>
        <p>Sign in to your account</p>
      </div>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Username</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input v-model="form.username" type="text" placeholder="Enter your username" required />
          </div>
        </div>
        <div class="form-group">
          <label>Password</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input v-model="form.password" type="password" placeholder="Enter your password" required />
          </div>
        </div>
        <button type="submit" class="submit-btn">Sign In</button>
      </form>
      <div class="auth-footer">
        <p>Don't have an account? <router-link to="/register">Sign Up</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import api, { authState } from '../services/api';
import { useRouter } from 'vue-router';

const router = useRouter();
const form = reactive({
  username: '',
  password: ''
});

const handleLogin = async () => {
  try {
    const res = await api.post('/user/login', form);
    if (res.code === 200) {
      authState.login(res.data);
      router.push('/');
    }
  } catch (e) {
    // Error handled by interceptor or console
  }
};
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
  padding: 2.5rem;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.auth-header h2 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.auth-header p {
  color: #7f8c8d;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: 500;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 8px;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.input-wrapper:focus-within {
  border-color: #4a90d9;
  background: white;
}

.input-icon {
  padding: 0 0.75rem;
  opacity: 0.5;
}

.input-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 0.85rem 0.75rem 0.85rem 0;
  font-size: 1rem;
}

.input-wrapper input:focus {
  outline: none;
  box-shadow: none;
}

.submit-btn {
  width: 100%;
  padding: 1rem;
  font-size: 1rem;
  margin-top: 1rem;
}

.auth-footer {
  text-align: center;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
  color: #7f8c8d;
}

.auth-footer a {
  color: #4a90d9;
  font-weight: 500;
}
</style>
