import { createApp } from 'vue'
import './style.css'  // 你的样式在前
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'  // Element Plus 样式在后
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.mount('#app')