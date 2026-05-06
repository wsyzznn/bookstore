<template>
  <div class="order-detail" v-if="details.length > 0">
    <h2>Order #{{ orderId }}</h2>
    <div class="info-section">
        <p><strong>Status:</strong> {{ details[0].status }}</p>
        <p><strong>Date:</strong> {{ details[0].createTime }}</p>
        <p><strong>Recipient:</strong> {{ details[0].recipientName }}</p>
        <p><strong>Total:</strong> ${{ details[0].totalPrice }}</p>
    </div>

    <table class="items-table">
        <thead>
            <tr>
                <th>Book</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Subtotal</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="item in details" :key="item.bookId">
                <td>{{ item.bookTitle }}</td>
                <td>${{ item.priceAtPurchase }}</td>
                <td>{{ item.quantity }}</td>
                <td>${{ item.itemSubTotal }}</td>
            </tr>
        </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '../services/api';

const route = useRoute();
const orderId = route.params.id;
const details = ref([]);

const fetchDetail = async () => {
  try {
    const res = await api.get('/order/detail', { params: { orderId } });
    if (res.code === 200) {
      details.value = res.data || [];
    }
  } catch (e) {
    console.error(e);
  }
};

onMounted(() => {
  fetchDetail();
});
</script>

<style scoped>
.info-section {
    margin-bottom: 2rem;
    padding: 1rem;
    background: #f9f9f9;
    border-radius: 8px;
}
.items-table {
    width: 100%;
    border-collapse: collapse;
}
.items-table th, .items-table td {
    padding: 0.75rem;
    border-bottom: 1px solid #eee;
    text-align: left;
}
</style>
