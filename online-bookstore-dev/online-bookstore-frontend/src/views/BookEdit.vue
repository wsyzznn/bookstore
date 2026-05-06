<template>
  <div class="book-edit">
    <h2>{{ isEdit ? 'Edit Book' : 'Add New Book' }}</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>ISBN</label>
        <input v-model="form.isbn" required :disabled="isEdit" />
      </div>
      <div class="form-group">
        <label>Title</label>
        <input v-model="form.title" required />
      </div>
      <div class="form-group">
        <label>Author</label>
        <input v-model="form.author" required />
      </div>
      <div class="form-group">
        <label>Category</label>
        <input v-model="form.category" required />
      </div>
      <div class="form-group">
        <label>Price</label>
        <input type="number" step="0.01" v-model="form.price" required />
      </div>
      <div class="form-group">
        <label>Stock Quantity</label>
        <input type="number" v-model="form.stockQuantity" required />
      </div>
      <div class="form-group">
        <label>Cover Image URL</label>
        <input v-model="form.coverImage" placeholder="/img/..." />
      </div>
      <div class="form-group">
        <label>Description</label>
        <textarea v-model="form.description"></textarea>
      </div>
      
      <button type="submit">Save</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api, { authState } from '../services/api';

const route = useRoute();
const router = useRouter();
const isEdit = computed(() => !!route.params.id);

const form = reactive({
  isbn: '',
  title: '',
  author: '',
  category: '',
  price: 0,
  stockQuantity: 0,
  coverImage: '',
  description: ''
});

const fetchBook = async () => {
  if (!isEdit.value) return;
  try {
    const res = await api.get(`/books/${route.params.id}`);
    if (res.code === 200) {
      Object.assign(form, res.data);
    }
  } catch (e) {
    console.error(e);
  }
};

const handleSubmit = async () => {
  try {
    const payload = { ...form };
    if (!isEdit.value) {
        payload.sellerId = authState.user.id;
    } else {
        payload.id = route.params.id;
    }

    const url = isEdit.value ? '/books/update' : '/books/add';
    const res = await api.post(url, payload);
    
    if (res.code === 200) {
      alert('Saved successfully');
      router.push('/merchant/books');
    }
  } catch (e) {
    // handled
  }
};

onMounted(() => {
  fetchBook();
});
</script>

<style scoped>
.book-edit {
  max-width: 600px;
  margin: 0 auto;
}
.form-group {
  margin-bottom: 1rem;
}
.form-group label {
  display: block;
  margin-bottom: 0.5rem;
}
.form-group input, .form-group textarea {
  width: 100%;
  padding: 0.5rem;
}
button {
  padding: 0.75rem 1.5rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
