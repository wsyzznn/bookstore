<template>
  <div class="home">
    <div class="search-section">
      <div class="search-bar">
        <div class="search-input-wrapper">
          <span class="search-icon">🔍</span>
          <input v-model="search.keyword" placeholder="Search books..." @keyup.enter="fetchBooks" />
        </div>
        <select v-model="search.type">
          <option value="title">Title</option>
          <option value="author">Author</option>
          <option value="category">Category</option>
        </select>
        <button @click="fetchBooks" class="search-btn">Search</button>
      </div>
    </div>

    <div class="book-list" v-if="books.length > 0">
      <div v-for="book in books" :key="book.bookId" class="book-card">
        <div class="book-cover-wrapper">
          <img :src="getImageUrl(book.coverImage)" alt="Cover" class="book-cover" />
          <div class="book-rating">
            <span>⭐</span> {{ book.averageRating?.toFixed(1) || '0.0' }}
          </div>
        </div>
        <div class="book-info">
          <h3 class="book-title">{{ book.title }}</h3>
          <p class="book-author">by {{ book.author }}</p>
          <p class="book-price">${{ book.price }}</p>
          <router-link :to="'/books/' + book.bookId" class="details-btn">
            View Details →
          </router-link>
        </div>
      </div>
    </div>
    
    <div v-else class="no-results">
      <div class="no-results-icon">📚</div>
      <h3>No books found</h3>
      <p>Try adjusting your search criteria</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import api from '../services/api';

const books = ref([]);
const search = reactive({
  keyword: '',
  type: 'title'
});

const getImageUrl = (path) => {
  if (!path) return 'https://via.placeholder.com/200x280?text=No+Cover';
  if (path.startsWith('http')) return path;
  return `http://localhost:8080${path}`;
};

const fetchBooks = async () => {
  try {
    const params = {};
    if (search.keyword) {
      params.keyword = search.keyword;
      params.type = search.type;
    }
    const res = await api.get('/books/search', { params });
    if (res.code === 200) {
      books.value = res.data || [];
    }
  } catch (e) {
    console.error(e);
  }
};

onMounted(() => {
  fetchBooks();
});
</script>

<style scoped>
.search-section {
  margin-bottom: 2rem;
}

.search-bar {
  display: flex;
  gap: 1rem;
  background: white;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 0 1rem;
}

.search-icon {
  margin-right: 0.5rem;
  opacity: 0.5;
}

.search-input-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 0.75rem 0;
  font-size: 1rem;
}

.search-input-wrapper input:focus {
  outline: none;
  box-shadow: none;
}

.search-bar select {
  min-width: 120px;
}

.search-btn {
  padding: 0.75rem 1.5rem;
}

.book-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1.5rem;
}

.book-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.book-cover-wrapper {
  position: relative;
  height: 280px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-rating {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.3rem 0.6rem;
  border-radius: 20px;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.book-info {
  padding: 1.25rem;
}

.book-title {
  font-size: 1.1rem;
  margin-bottom: 0.3rem;
  color: #2c3e50;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-author {
  color: #7f8c8d;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.book-price {
  font-size: 1.3rem;
  font-weight: 700;
  color: #e74c3c;
  margin-bottom: 1rem;
}

.details-btn {
  display: block;
  text-align: center;
  padding: 0.7rem;
  background: linear-gradient(135deg, #4a90d9, #357abd);
  color: white !important;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
}

.details-btn:hover {
  background: linear-gradient(135deg, #357abd, #2c6aa0);
}

.no-results {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.no-results-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.no-results h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.no-results p {
  color: #7f8c8d;
}
</style>
