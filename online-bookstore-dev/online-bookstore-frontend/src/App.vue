<template>
  <div id="app">
    <nav class="navbar">
      <router-link to="/" class="logo">
        <span class="logo-icon">📚</span>
        <span class="logo-text">Online Bookstore</span>
      </router-link>
      <div class="nav-links">
        <router-link to="/" class="nav-link">
          <span>🏠</span> Home
        </router-link>
        
        <!-- Customer Links -->
        <template v-if="authState.hasRole('CUSTOMER')">
          <router-link to="/cart" class="nav-link">
            <span>🛒</span> Cart
          </router-link>
          <router-link to="/my-orders" class="nav-link">
            <span>📦</span> My Orders
          </router-link>
          <router-link to="/addresses" class="nav-link">
            <span>📍</span> Address Book
          </router-link>
        </template>

        <!-- Merchant Links -->
        <template v-if="authState.hasRole('MERCHANT')">
          <router-link to="/merchant/books" class="nav-link">
            <span>📖</span> My Books
          </router-link>
          <router-link to="/merchant/orders" class="nav-link">
            <span>📋</span> Orders
          </router-link>
          <router-link to="/merchant/books/add" class="nav-link">
            <span>➕</span> Add Book
          </router-link>
        </template>

        <!-- Admin Links -->
        <template v-if="authState.hasRole('ADMIN')">
          <router-link to="/admin/books" class="nav-link">
            <span>📚</span> All Books
          </router-link>
          <router-link to="/admin/users" class="nav-link">
            <span>👥</span> Users
          </router-link>
          <router-link to="/admin/orders" class="nav-link">
            <span>📊</span> All Orders
          </router-link>
        </template>

        <div class="auth-section">
          <template v-if="!authState.isAuthenticated">
            <router-link to="/login" class="auth-btn login-btn">Login</router-link>
            <router-link to="/register" class="auth-btn register-btn">Register</router-link>
          </template>
          <template v-else>
            <div class="user-info">
              <span class="avatar">{{ authState.user.username.charAt(0).toUpperCase() }}</span>
              <span class="username">{{ authState.user.username }}</span>
              <span class="role-badge">{{ authState.user.role }}</span>
            </div>
            <button @click="logout" class="logout-btn">Logout</button>
          </template>
        </div>
      </div>
    </nav>
    
    <main class="main-content">
      <div class="hero-section" v-if="$route.path === '/'">
        <div class="hero-bg"></div>
        <div class="hero-content">
          <h1>Welcome to Online Bookstore</h1>
          <p>Discover your next favorite book from our vast collection</p>
        </div>
      </div>
      <div class="container">
        <router-view></router-view>
      </div>
    </main>

    <footer class="footer">
      <p>© 2025 Online Bookstore. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { authState } from './services/api';
import api from './services/api';
import { useRouter } from 'vue-router';

const router = useRouter();

const logout = async () => {
  try {
    await api.post('/user/logout');
  } catch (e) {
    console.error("Logout failed", e);
  } finally {
    authState.logout();
    router.push('/login');
  }
};
</script>

<style>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
  height: 64px;
  background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.4rem;
  font-weight: 700;
  color: white !important;
  text-decoration: none !important;
}

.logo-icon {
  font-size: 1.6rem;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  color: rgba(255, 255, 255, 0.9) !important;
  text-decoration: none !important;
  padding: 0.5rem 0.8rem;
  border-radius: 6px;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.15);
  color: white !important;
}

.nav-link.router-link-active {
  background: rgba(255, 255, 255, 0.2);
}

.auth-section {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-left: 1.5rem;
  padding-left: 1.5rem;
  border-left: 1px solid rgba(255, 255, 255, 0.2);
}

.auth-btn {
  padding: 0.45rem 1rem;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  text-decoration: none !important;
  transition: all 0.2s;
}

.login-btn {
  color: white !important;
  background: rgba(255, 255, 255, 0.1);
}

.login-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.register-btn {
  color: #2c3e50 !important;
  background: white;
}

.register-btn:hover {
  background: #f0f0f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f39c12, #e74c3c);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.9rem;
}

.username {
  font-weight: 500;
}

.role-badge {
  font-size: 0.7rem;
  padding: 0.15rem 0.5rem;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  text-transform: uppercase;
}

.logout-btn {
  background: rgba(231, 76, 60, 0.8) !important;
  color: white;
  padding: 0.4rem 0.8rem;
  font-size: 0.85rem;
  box-shadow: none;
}

.logout-btn:hover {
  background: #e74c3c !important;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.hero-section {
  position: relative;
  padding: 4rem 2rem;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-section h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.hero-section p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.container {
  padding: 2rem;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  flex: 1;
}

.footer {
  background: #2c3e50;
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  padding: 1.5rem;
  font-size: 0.9rem;
}
</style>

