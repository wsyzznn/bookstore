<template>
  <div class="admin-orders">
    <h2>Order Management</h2>
    <table class="orders-table">
      <thead>
        <tr>
          <th>Order ID</th>
          <th>Book</th>
          <th>Recipient</th>
          <th>Status</th>
          <th>Subtotal</th>
          <th>Total (Order)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.orderId + '-' + order.bookTitle">
          <td>{{ order.orderId }}</td>
          <td>{{ order.bookTitle }}</td>
          <td>{{ order.recipientName }}</td>
          <td>{{ order.status }}</td>
          <td>${{ order.itemSubTotal }}</td>
          <td>${{ order.totalPrice }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const orders = ref([]);

const fetchOrders = async () => {
  try {
    const res = await api.get('/admin/orders');
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
.orders-table {
  width: 100%;
  border-collapse: collapse;
}
.orders-table th, .orders-table td {
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
  text-align: left;
}
</style>
