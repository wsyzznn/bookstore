<template>
  <div class="address-book">
    <h2>My Address Book</h2>
    <button @click="showAddForm = true" class="add-btn">Add New Address</button>
    
    <div class="address-list">
      <div v-for="addr in addresses" :key="addr.id" class="address-card">
        <p><strong>{{ addr.recipientName }}</strong></p>
        <p>{{ addr.phoneNumber }}</p>
        <p>{{ addr.addressDetail }}</p>
        <div class="actions">
          <button @click="editAddress(addr)">Edit</button>
          <button @click="deleteAddress(addr.id)" class="delete-btn">Delete</button>
        </div>
      </div>
    </div>

    <!-- Modal for Add/Edit -->
    <div v-if="showAddForm || editingAddress" class="modal">
      <div class="modal-content">
        <h3>{{ editingAddress ? 'Edit Address' : 'Add Address' }}</h3>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>Recipient Name</label>
            <input v-model="form.recipientName" required />
          </div>
          <div class="form-group">
            <label>Phone Number</label>
            <input v-model="form.phoneNumber" required />
          </div>
          <div class="form-group">
            <label>Address Detail</label>
            <textarea v-model="form.addressDetail" required></textarea>
          </div>
          <div class="modal-actions">
            <button type="submit">Save</button>
            <button type="button" @click="closeModal" class="cancel-btn">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import api, { authState } from '../services/api';

const addresses = ref([]);
const showAddForm = ref(false);
const editingAddress = ref(null);

const form = reactive({
  recipientName: '',
  phoneNumber: '',
  addressDetail: ''
});

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

const editAddress = (addr) => {
  editingAddress.value = addr;
  form.recipientName = addr.recipientName;
  form.phoneNumber = addr.phoneNumber;
  form.addressDetail = addr.addressDetail;
};

const closeModal = () => {
  showAddForm.value = false;
  editingAddress.value = null;
  form.recipientName = '';
  form.phoneNumber = '';
  form.addressDetail = '';
};

const handleSubmit = async () => {
  try {
    const payload = { ...form, userId: authState.user.id };
    let res;
    if (editingAddress.value) {
      payload.id = editingAddress.value.id;
      res = await api.post('/addresses/update', payload);
    } else {
      res = await api.post('/addresses/add', payload);
    }

    if (res.code === 200) {
      fetchAddresses();
      closeModal();
    }
  } catch (e) {
    // handled
  }
};

const deleteAddress = async (id) => {
  if (!confirm('Are you sure?')) return;
  try {
    const res = await api.post('/addresses/delete', { id });
    if (res.code === 200) {
      fetchAddresses();
    }
  } catch (e) {
    // handled
  }
};

onMounted(() => {
  fetchAddresses();
});
</script>

<style scoped>
.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}
.address-card {
  border: 1px solid #ddd;
  padding: 1rem;
  border-radius: 8px;
}
.actions {
  margin-top: 1rem;
  display: flex;
  gap: 0.5rem;
}
.add-btn {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}
.delete-btn {
  background-color: #ff4444;
  color: white;
}
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 400px;
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
.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}
.cancel-btn {
  background-color: #ccc;
}
</style>
