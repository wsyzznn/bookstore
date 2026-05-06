<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-icon">✨</div>
        <h2>Create Account</h2>
        <p>Join us today</p>
      </div>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Username</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input v-model="form.username" type="text" placeholder="Choose a username" required />
          </div>
        </div>
        <div class="form-group">
          <label>Password</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input v-model="form.password" type="password" placeholder="Create a password" required />
          </div>
        </div>
        <div class="form-group">
          <label>Email</label>
          <div class="input-wrapper">
            <span class="input-icon">📧</span>
            <input v-model="form.email" type="email" placeholder="Enter your email" required />
          </div>
        </div>
        <div class="form-group">
          <label>Account Type</label>
          <div class="role-selector">
            <label class="role-option" :class="{ active: form.role === 'CUSTOMER' }">
              <input type="radio" v-model="form.role" value="CUSTOMER" />
              <span class="role-icon">🛒</span>
              <span class="role-name">Customer</span>
              <span class="role-desc">Buy books</span>
            </label>
            <label class="role-option" :class="{ active: form.role === 'MERCHANT' }">
              <input type="radio" v-model="form.role" value="MERCHANT" />
              <span class="role-icon">📦</span>
              <span class="role-name">Merchant</span>
              <span class="role-desc">Sell books</span>
            </label>
          </div>
        </div>
        <button type="submit" class="submit-btn">Create Account</button>
      </form>
      <div class="auth-footer">
        <p>Already have an account? <router-link to="/login">Sign In</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import api from '../services/api';
import { useRouter } from 'vue-router';

const router = useRouter();
const form = reactive({
  username: '',
  password: '',
  email: '',
  role: 'CUSTOMER'
});

const handleRegister = async () => {
  try {
    const res = await api.post('/user/register', form);
    if (res.code === 200) {
      alert('Registration successful! Please login.');
      router.push('/login');
    }
  } catch (e) {
    // Error handled by interceptor
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
  max-width: 480px;
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

.role-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.role-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem;
  border: 2px solid #e1e5eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}

.role-option input {
  display: none;
}

.role-option:hover {
  border-color: #4a90d9;
}

.role-option.active {
  border-color: #4a90d9;
  background: #f0f7ff;
}

.role-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.role-name {
  font-weight: 600;
  color: #2c3e50;
}

.role-desc {
  font-size: 0.8rem;
  color: #7f8c8d;
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
