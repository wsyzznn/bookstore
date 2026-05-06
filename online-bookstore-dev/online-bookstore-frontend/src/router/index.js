import { createRouter, createWebHistory } from 'vue-router';
import { authState } from '../services/api';

// Lazy load views
const Home = () => import('../views/Home.vue');
const Login = () => import('../views/Login.vue');
const Register = () => import('../views/Register.vue');
const BookDetail = () => import('../views/BookDetail.vue');
const Cart = () => import('../views/Cart.vue');
const Checkout = () => import('../views/Checkout.vue');
const MyOrders = () => import('../views/MyOrders.vue');
const OrderDetail = () => import('../views/OrderDetail.vue');
const AddressBook = () => import('../views/AddressBook.vue');
const MerchantBooks = () => import('../views/MerchantBooks.vue');
const BookEdit = () => import('../views/BookEdit.vue');
const AdminUsers = () => import('../views/AdminUsers.vue');
const AdminOrders = () => import('../views/AdminOrders.vue');

const routes = [
    { path: '/', component: Home, name: 'Home' },
    { path: '/login', component: Login, name: 'Login' },
    { path: '/register', component: Register, name: 'Register' },
    { path: '/books/:id', component: BookDetail, name: 'BookDetail' },
    { path: '/cart', component: Cart, name: 'Cart', meta: { requiresAuth: true } },
    { path: '/checkout', component: Checkout, name: 'Checkout', meta: { requiresAuth: true } },
    { path: '/my-orders', component: MyOrders, name: 'MyOrders', meta: { requiresAuth: true } },
    { path: '/order/:id', component: OrderDetail, name: 'OrderDetail', meta: { requiresAuth: true } },
    { path: '/addresses', component: AddressBook, name: 'AddressBook', meta: { requiresAuth: true } },
    
    // Merchant Routes
    { path: '/merchant/books', component: MerchantBooks, name: 'MerchantBooks', meta: { requiresAuth: true, role: 'MERCHANT' } },
    { path: '/merchant/books/add', component: BookEdit, name: 'AddBook', meta: { requiresAuth: true, role: 'MERCHANT' } },
    { path: '/merchant/books/edit/:id', component: BookEdit, name: 'EditBook', meta: { requiresAuth: true, role: 'MERCHANT' } },
    { path: '/merchant/orders', component: AdminOrders, name: 'MerchantOrders', meta: { requiresAuth: true, role: 'MERCHANT' } }, // Reusing AdminOrders for Merchant view as per spec logic

    // Admin Routes
    { path: '/admin/users', component: AdminUsers, name: 'AdminUsers', meta: { requiresAuth: true, role: 'ADMIN' } },
    { path: '/admin/orders', component: AdminOrders, name: 'AdminOrders', meta: { requiresAuth: true, role: 'ADMIN' } },
    { path: '/admin/books', component: MerchantBooks, name: 'AdminBooks', meta: { requiresAuth: true, role: 'ADMIN' } }, // Admin can manage all books? Spec says "All Books Manage" for Admin. Reusing MerchantBooks component but might need adjustment.
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    if (to.meta.requiresAuth && !authState.isAuthenticated) {
        next('/login');
    } else if (to.meta.role && authState.user.role !== to.meta.role && authState.user.role !== 'ADMIN') {
        // Allow ADMIN to access MERCHANT routes if implied, but spec is strict. 
        // Spec: Merchant: My Books. Admin: All Books Manage.
        // Let's stick to strict role check or allow Admin override if logic permits.
        // For now, strict check based on route meta.
        if (to.meta.role === 'MERCHANT' && authState.user.role === 'ADMIN') {
             next(); // Allow admin to access merchant routes if needed (e.g. book management)
        } else {
             next('/'); // Redirect to home if unauthorized
        }
    } else {
        next();
    }
});

export default router;
