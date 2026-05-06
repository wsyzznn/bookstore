<template>
  <div class="checkout">
    <h2>Checkout</h2>
    
    <div class="section">
      <h3>1. Confirm Order Items</h3>
      <div v-if="cartItems.length > 0">
        <ul>
          <li v-for="item in cartItems" :key="item.bookId">
            {{ item.title }} x {{ item.quantity }} - ${{ (item.price * item.quantity).toFixed(2) }}
          </li>
        </ul>
        <p class="total">Total: ${{ totalPrice }}</p>
      </div>
    </div>

    <div class="section">
      <h3>2. Select Shipping Address</h3>
      <div v-if="addresses.length > 0" class="address-selection">
        <div v-for="addr in addresses" :key="addr.id" class="address-option">
          <input type="radio" :id="'addr-'+addr.id" :value="addr.id" v-model="selectedAddressId" />
          <label :for="'addr-'+addr.id">
            <strong>{{ addr.recipientName }}</strong> - {{ addr.phoneNumber }}<br/>
            {{ addr.addressDetail }}
          </label>
        </div>
      </div>
      <div v-else>
        <p>No addresses found. <router-link to="/addresses">Add an address</router-link></p>
      </div>
    </div>

    <button @click="placeOrder" :disabled="!selectedAddressId || cartItems.length === 0" class="place-order-btn">
      Confirm Payment
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api, { authState } from '../services/api';
import { useRouter } from 'vue-router';

const router = useRouter();
const cartItems = ref([]);
const addresses = ref([]);
const selectedAddressId = ref(null);

const fetchCart = async () => {
  try {
    const res = await api.get('/cart');
    if (res.code === 200) {
      cartItems.value = res.data || [];
    }
  } catch (e) {
    console.error(e);
  }
};

const fetchAddresses = async () => {
  try {
    const res = await api.get('/addresses', { params: { userId: authState.user.id } });
    if (res.code === 200) {
      addresses.value = res.data || [];
    }
  } catch (e) {
    console.error(e);
  }
};

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0).toFixed(2);
});

const placeOrder = async () => {
  try {
    const res = await api.post('/order/checkout', {
      userId: authState.user.id,
      addressId: selectedAddressId.value
    });
    if (res.code === 200) {
      alert('Order placed successfully!');
      router.push('/my-orders');
    }
  } catch (e) {
    // handled
  }
};

onMounted(() => {
  fetchCart();
  fetchAddresses();
});
</script>

<style scoped>
.section {
  margin-bottom: 2rem;
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
}
.address-option {
  margin-bottom: 1rem;
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}
.place-order-btn {
  width: 100%;
  padding: 1rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1.2rem;
  cursor: pointer;
}
.place-order-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
