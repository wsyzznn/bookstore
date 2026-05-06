<template>
  <div class="my-orders">
    <h2>My Orders</h2>

    <div class="order-list">
      <div v-for="order in orders" :key="order.orderId" class="order-card">
        <div class="order-header">
          <span>Order #{{ order.orderId }}</span>
          <span>{{ order.createTime }}</span>
          <span :class="order.status">{{ order.status }}</span>
        </div>
        <div class="order-body">
          <p>Total: ${{ order.totalPrice }}</p>
          <p>Items: {{ order.bookCount }}</p>
          <router-link :to="'/order/' + order.orderId" class="detail-link">View Details</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api, { authState } from '../services/api';

const orders = ref([]);

const fetchOrders = async () => {
  try {
    const res = await api.get('/order/my-orders', { params: { userId: authState.user.id } });
    if (res.code === 200) {
      orders.value = res.data || [];
    }
  } catch (e) {
    console.error(e);
  }
};

onMounted(() => {
  fetchOrders();
});
</script>

<style scoped>
.order-card {
  border: 1px solid #ddd;
  margin-bottom: 1rem;
  border-radius: 8px;
  overflow: hidden;
}
.order-header {
  background: #f5f5f5;
  padding: 0.5rem 1rem;
  display: flex;
  justify-content: space-between;
}
.order-body {
  padding: 1rem;
}
.detail-link {
  color: #42b983;
  text-decoration: none;
}
</style>
