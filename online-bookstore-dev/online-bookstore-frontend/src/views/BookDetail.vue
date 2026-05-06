<template>
  <div class="book-detail" v-if="book">
    <div class="detail-container">
      <img :src="getImageUrl(book.coverImage)" class="cover-large" />
      <div class="info">
        <h2>{{ book.title }}</h2>
        <p><strong>Author:</strong> {{ book.author }}</p>
        <p><strong>ISBN:</strong> {{ book.isbn }}</p>
        <p><strong>Price:</strong> ${{ book.price }}</p>
        <p><strong>Stock:</strong> {{ book.stockQuantity }}</p>
        <p><strong>Rating:</strong> {{ book.averageRating }} / 5</p>
        <p class="description">{{ book.description }}</p>
        
        <div class="actions" v-if="authState.hasRole('CUSTOMER')">
          <div class="quantity-selector">
            <label>Quantity:</label>
            <input type="number" v-model="quantity" min="1" :max="book.stockQuantity" />
          </div>
          <button @click="addToCart" :disabled="book.stockQuantity < 1">Add to Cart</button>
        </div>

        <div class="rating-section" v-if="authState.hasRole('CUSTOMER')">
            <h3>Rate this book</h3>
            <select v-model="myRating">
                <option value="0">Select Rating</option>
                <option v-for="n in 5" :key="n" :value="n">{{ n }} Stars</option>
            </select>
            <button @click="submitRating">Submit Rating</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api, { authState } from '../services/api';

const route = useRoute();
const book = ref(null);
const quantity = ref(1);
const myRating = ref(0);

const getImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `http://localhost:8080${path}`;
};

const fetchBook = async () => {
  try {
    const res = await api.get(`/books/${route.params.id}`);
    // `api` interceptor returns `res.data` when code===200, otherwise original wrapper.
    // Support both shapes: wrapper ({code,msg,data}) or direct data (book object).
    const payload = (res && res.data) ? res.data : res;

    // Normalize fields expected by this component
    book.value = {
      ...payload,
      coverImage: payload.coverImage || payload.cover || '',
      stockQuantity: payload.stockQuantity != null ? payload.stockQuantity : (payload.stock != null ? payload.stock : 0),
      averageRating: payload.averageRating != null ? payload.averageRating : (payload.avgRating || 0),
      bookId: payload.bookId || payload.id
    };

    if (payload.myRating) {
      myRating.value = payload.myRating;
    }
  } catch (e) {
    console.error(e);
  }
};

const addToCart = async () => {
  try {
    // primary call: /cart/add
    const res = await api.post('/cart/add', {
      bookId: book.value.bookId || book.value.id,
      quantity: quantity.value
    });
    // interceptor returns inner data on 200; show success unconditionally if no error thrown
    alert('Added to cart');
  } catch (e) {
    // fallback: some environments route to /api/cart
    try {
      await api.post('/cart', {
        bookId: book.value.bookId || book.value.id,
        quantity: quantity.value
      });
      alert('Added to cart');
    } catch (err) {
      console.error('Add to cart failed:', err);
    }
  }
};

const submitRating = async () => {
  try {
    const res = await api.post('/books/rate', {
      bookId: book.value.bookId || book.value.id,
      rating: myRating.value,
      userId: authState.user ? authState.user.id : undefined
    });

    // `api` returns the inner data on success (or throws). Update UI from returned summary if present.
    if (res) {
      if (res.averageRating != null) {
        book.value.averageRating = res.averageRating;
      }
      alert('Rating submitted');
      // ensure latest details
      fetchBook();
    }
  } catch (e) {
    // handled by interceptor
  }
}

onMounted(() => {
  fetchBook();
});
</script>

<style scoped>
.detail-container {
  display: flex;
  gap: 2rem;
}
.cover-large {
  width: 300px;
  object-fit: cover;
}
.info {
  flex: 1;
}
.description {
  margin: 1rem 0;
  line-height: 1.6;
}
.actions {
  margin-top: 2rem;
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 8px;
}
.quantity-selector {
  margin-bottom: 1rem;
}
button {
  padding: 0.75rem 1.5rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
.rating-section {
    margin-top: 2rem;
    border-top: 1px solid #eee;
    padding-top: 1rem;
}
</style>
