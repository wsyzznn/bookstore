import axios from 'axios';
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';

// Global state for user
export const authState = reactive({
    user: JSON.parse(localStorage.getItem('user')) || null,
    isAuthenticated: !!localStorage.getItem('user'),
    
    login(userData) {
        this.user = userData;
        this.isAuthenticated = true;
        localStorage.setItem('user', JSON.stringify(userData));
    },
    
    logout() {
        this.user = null;
        this.isAuthenticated = false;
        localStorage.removeItem('user');
    },

    hasRole(role) {
        return this.user && this.user.role === role;
    }
});

// Axios instance
const api = axios.create({
    baseURL: '/api', // Using proxy in vite.config.js is better, or http://localhost:8080/api if CORS allowed
    withCredentials: true, // Important for Cookies
    headers: {
        'Content-Type': 'application/json'
    }
});

// Interceptor from spec
api.interceptors.response.use(res => {
    const code = res.data.code;
    if (code === 200) {
        return res.data;
    } else if (code === 401) {
        const msg = res.data.msg || "登录已过期，请重新登录";
        ElMessage.error(msg); // 使用ElMessage
        authState.logout();
        window.location.href = '/login';
        return Promise.reject("Unauthorized");
    } else if (code === 403) {
        const msg = res.data.msg || "权限不足";
        ElMessage.error(msg); // 使用ElMessage
        return Promise.reject("Forbidden");
    } else {
        const msg = res.data.msg || "系统错误";
        ElMessage.error(msg); // 使用ElMessage
        return Promise.reject(res.data.msg);
    }
}, error => {
    let message = '网络错误，请检查网络连接';
    if (error.response) {
        message = error.response.data?.msg || `请求失败 (${error.response.status})`;
    } else if (error.request) {
        message = '服务器无响应，请稍后重试';
    }
    
    ElMessage.error(message); // 使用ElMessage
    console.error('API Error:', error.message);
    return Promise.reject(error);
});

export default api;
