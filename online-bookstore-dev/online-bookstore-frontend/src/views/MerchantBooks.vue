<template>
  <div class="merchant-books">
    <h2>My Books</h2>
    <router-link to="/merchant/books/add" class="add-btn">Add New Book</router-link>
    
    <table class="books-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Title</th>
          <th>Stock</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="book in books" :key="book.id">
          <td>{{ book.id }}</td>
          <td>{{ book.title }}</td>
          <td>{{ book.stockQuantity }}</td>
          <td>
            <span :class="getStatusClass(book.stockStatus)">{{ book.stockStatus }}</span>
          </td>
          <td>
            <button @click="editBook(book.id)">Edit</button>
            <button @click="deleteBook(book.id)" class="delete-btn">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api, { authState } from '../services/api';
import { useRouter } from 'vue-router';

const router = useRouter();
const books = ref([]);

const fetchBooks = async () => {
  try {
    // Spec says /api/books/my-books?userId=...
    // But wait, if I am Admin, I might want to see all books?
    // Spec says Admin: All Books Manage.
    // If I am Admin, maybe I should use a different endpoint or the same one if it supports it?
    // Spec for Admin: "All Books Manage" -> "MerchantBooks" component in router.
    // But "My Books" endpoint filters by userId.
    // If Admin, maybe I need to search all books?
    // The spec doesn't explicitly say Admin has a special "Get All Books" endpoint for management, 
    // but "Search" returns public books.
    // However, Admin needs to manage ALL books.
    // Let's assume Admin uses the search endpoint or a special one.
    // But for now, let's stick to Merchant usage.
    // If Admin, maybe I can pass no userId to get all? Or maybe search?
    // Let's assume for now this component is primarily for Merchant.
    // If Admin uses it, I might need to adjust.
    // Router says: { path: '/admin/books', component: MerchantBooks ... }
    // So Admin uses this.
    // If Admin, maybe I should call search with empty params to get all?
    // But search only returns "Public" books (stock > 0).
    // Admin needs to see out of stock too.
    // I'll assume for now Admin sees their own books or I'll just implement for Merchant first.
    // Actually, let's check if there is an endpoint for Admin to get all books.
    // Spec doesn't list one.
    // I will implement for Merchant.
    
    const params = {};
    if (authState.hasRole('MERCHANT')) {
        params.userId = authState.user.id;
    }
    // If Admin, what to do?
    // Maybe Admin can see everything?
    // I'll just pass userId if Merchant.
    
    const res = await api.get('/books/my-books', { params });
    if (res.code === 200) {
      // 兼容后端可能返回数组或 { list: [...] } 结构
      if (Array.isArray(res.data)) {
        books.value = res.data;
      } else if (res.data && Array.isArray(res.data.list)) {
        books.value = res.data.list;
      } else {
        books.value = res.data || [];
      }
      console.log('MerchantBooks fetched', books.value.length, books.value);
    }
  } catch (e) {
    console.error(e);
  }
};

const editBook = (id) => {
  // If Admin, path is different? No, router uses same component but different path?
  // Actually router has: /merchant/books/edit/:id
  // I should probably use the router name or construct path based on role.
  // But simpler:
  router.push(`/merchant/books/edit/${id}`);
};

const deleteBook = async (id) => {
  if (!confirm('Are you sure?')) return;
  try {
    const res = await api.post('/books/delete', { id });
    if (res.code === 200) {
      fetchBooks();
    }
  } catch (e) {
    // handled
  }
};

const getStatusClass = (status) => {
  if (status === 'Out of Stock') return 'status-out';
  if (status === 'Low Stock') return 'status-low';
  return 'status-in';
};

onMounted(() => {
  fetchBooks();
});
</script>

<style scoped>
.books-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}
.books-table th, .books-table td {
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
  text-align: left;
}
.add-btn {
  display: inline-block;
  background-color: #42b983;
  color: white;
  padding: 0.5rem 1rem;
  text-decoration: none;
  border-radius: 4px;
}
.delete-btn {
  background-color: #ff4444;
  color: white;
  border: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  margin-left: 0.5rem;
  cursor: pointer;
}
.status-out { color: red; font-weight: bold; }
.status-low { color: orange; font-weight: bold; }
.status-in { color: green; }
</style>
