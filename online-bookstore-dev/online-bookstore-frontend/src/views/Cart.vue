<template>
  <div class="cart-page">
    <div class="page-header">
      <h2>🛒 Shopping Cart</h2>
    </div>
    
    <div v-if="cartItems.length > 0" class="cart-content">
      <div class="cart-items">
        <div v-for="item in cartItems" :key="item.bookId" class="cart-item">
          <img :src="getImageUrl(item.coverImage)" class="item-image" />
          <div class="item-details">
            <h3 class="item-title">{{ item.title }}</h3>
            <p class="item-price">${{ item.price }}</p>
          </div>
          <div class="item-quantity">
            <button @click="decreaseQty(item)" class="qty-btn">−</button>
            <span class="qty-value">{{ item.quantity }}</span>
            <button @click="increaseQty(item)" class="qty-btn">+</button>
          </div>
          <div class="item-total">
            ${{ (item.price * item.quantity).toFixed(2) }}
          </div>
          <button @click="removeItem(item.bookId)" class="remove-btn">
            🗑️
          </button>
        </div>
      </div>
      
      <div class="cart-summary">
        <h3>Order Summary</h3>
        <div class="summary-row">
          <span>Subtotal</span>
          <span>${{ totalPrice }}</span>
        </div>
        <div class="summary-row">
          <span>Shipping</span>
          <span class="free">FREE</span>
        </div>
        <div class="summary-total">
          <span>Total</span>
          <span>${{ totalPrice }}</span>
        </div>
        <router-link to="/checkout" class="checkout-btn">
          Proceed to Checkout →
        </router-link>
      </div>
    </div>
    
    <div v-else class="empty-cart">
      <div class="empty-icon">🛒</div>
      <h3>Your cart is empty</h3>
      <p>Looks like you haven't added anything to your cart yet.</p>
      <router-link to="/" class="shop-btn">Start Shopping</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../services/api';

const cartItems = ref([]);

const getImageUrl = (path) => {
  if (!path) return 'https://via.placeholder.com/80x100?text=Book';
  if (path.startsWith('http')) return path;
  return `http://localhost:8080${path}`;
};

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

const updateQuantity = async (item) => {
  try {
    await api.post('/cart/update', {
      bookId: item.bookId,
      quantity: item.quantity
    });
  } catch (e) {
    fetchCart();
  }
};

const increaseQty = (item) => {
  item.quantity++;
  updateQuantity(item);
};

const decreaseQty = (item) => {
  if (item.quantity > 1) {
    item.quantity--;
    updateQuantity(item);
  }
};

const removeItem = async (bookId) => {
  try {
    await api.post('/cart/delete', { bookId });
    cartItems.value = cartItems.value.filter(item => item.bookId !== bookId);
  } catch (e) {
    console.error(e);
  }
};

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0).toFixed(2);
});

onMounted(() => {
  fetchCart();
});
</script>

<style scoped>
.cart-page {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header h2 {
  margin-bottom: 2rem;
}

.cart-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 2rem;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  background: white;
  padding: 1.25rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.item-image {
  width: 80px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

.item-details {
  flex: 1;
}

.item-title {
  font-size: 1rem;
  margin-bottom: 0.25rem;
  color: #2c3e50;
}

.item-price {
  color: #7f8c8d;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.qty-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  padding: 0;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qty-value {
  width: 40px;
  text-align: center;
  font-weight: 600;
}

.item-total {
  font-size: 1.1rem;
  font-weight: 700;
  color: #2c3e50;
  min-width: 80px;
  text-align: right;
}

.remove-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.5rem;
  opacity: 0.5;
  transition: opacity 0.2s;
  box-shadow: none;
}

.remove-btn:hover {
  opacity: 1;
  transform: none;
}

.cart-summary {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: fit-content;
  position: sticky;
  top: 80px;
}

.cart-summary h3 {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
  color: #7f8c8d;
}

.free {
  color: #42b983;
  font-weight: 500;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  font-size: 1.25rem;
  font-weight: 700;
  color: #2c3e50;
  padding-top: 1rem;
  margin-top: 1rem;
  border-top: 2px solid #eee;
}

.checkout-btn {
  display: block;
  text-align: center;
  padding: 1rem;
  margin-top: 1.5rem;
  background: linear-gradient(135deg, #42b983, #36a372);
  color: white !important;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.2s;
}

.checkout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.4);
}

.empty-cart {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-cart h3 {
  color: #2c3e50;
}

.empty-cart p {
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.shop-btn {
  display: inline-block;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #4a90d9, #357abd);
  color: white !important;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
}
</style>
