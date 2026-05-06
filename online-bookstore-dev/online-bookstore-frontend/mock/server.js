/**
 * Mock Server - 基于 json-server 的本地 Mock 服务
 * 启动命令: npm run mock
 * 
 * 字段名已与 SQL 表结构对齐：
 * - books: seller_id, stock_quantity, cover_image
 * - users: balance, role, create_time
 * - addresses: recipient_name, phone_number, address_detail
 * - orders: user_id, total_price, recipient_name, recipient_phone, shipping_address, create_time
 * - order_items: order_id, book_id, price_at_purchase
 */

import jsonServer from 'json-server';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const server = jsonServer.create();
const router = jsonServer.router(join(__dirname, 'data.json'));
const middlewares = jsonServer.defaults();

server.set('etag', false);

server.use((req, res, next) => {
  res.setHeader('Cache-Control', 'no-store, no-cache, must-revalidate, proxy-revalidate');
  res.setHeader('Pragma', 'no-cache');
  res.setHeader('Expires', '0');
  next();
});

const wrapResponse = (data, code = 200, msg = 'success') => ({
  code,
  msg,
  data
});

server.use(middlewares);
server.use(jsonServer.bodyParser);

// ===================== 认证相关 =====================
server.post('/api/auth/login', (req, res) => {
  const { username, password } = req.body;
  const db = router.db;
  const user = db.get('users').find({ username, password }).value();
  
  if (user) {
    const { password: _, ...userWithoutPassword } = user;
    res.json(wrapResponse(userWithoutPassword));
  } else {
    res.json(wrapResponse(null, 401, '用户名或密码错误'));
  }
});

server.post('/api/auth/register', (req, res) => {
  const { username, password, email } = req.body;
  const db = router.db;
  
  const existingUser = db.get('users').find({ username }).value();
  if (existingUser) {
    return res.json(wrapResponse(null, 400, '用户名已存在'));
  }
  
  const newUser = {
    id: Date.now(),
    username,
    password,
    email,
    balance: 99999.00,
    role: 'CUSTOMER',
    create_time: new Date().toISOString().replace('T', ' ').substring(0, 19)
  };
  
  db.get('users').push(newUser).write();
  const { password: _, ...userWithoutPassword } = newUser;
  res.json(wrapResponse(userWithoutPassword));
});

server.post('/api/auth/logout', (req, res) => {
  res.json(wrapResponse(null, 200, '退出成功'));
});

server.get('/api/auth/current', (req, res) => {
  const db = router.db;
  const userId = req.query.userId ? parseInt(req.query.userId) : 3;
  const user = db.get('users').find({ id: userId }).value();
  if (!user) {
    return res.json(wrapResponse(null, 404, '用户未登录'));
  }
  const { password, ...userWithoutPassword } = user;
  res.json(wrapResponse(userWithoutPassword));
});

server.post('/api/user/login', (req, res) => {
  const { username, password } = req.body;
  const db = router.db;
  const user = db.get('users').find({ username, password }).value();
  
  if (user) {
    const { password: _, ...userWithoutPassword } = user;
    res.json(wrapResponse(userWithoutPassword));
  } else {
    res.json(wrapResponse(null, 401, '用户名或密码错误'));
  }
});

server.post('/api/user/register', (req, res) => {
  const { username, password, email } = req.body;
  const db = router.db;
  
  const existingUser = db.get('users').find({ username }).value();
  if (existingUser) {
    return res.json(wrapResponse(null, 400, '用户名已存在'));
  }
  
  const newUser = {
    id: Date.now(),
    username,
    password,
    email,
    balance: 99999.00,
    role: 'CUSTOMER',
    create_time: new Date().toISOString().replace('T', ' ').substring(0, 19)
  };
  
  db.get('users').push(newUser).write();
  const { password: _, ...userWithoutPassword } = newUser;
  res.json(wrapResponse(userWithoutPassword));
});

server.post('/api/user/logout', (req, res) => {
  res.json(wrapResponse(null, 200, '退出成功'));
});

server.post('/api/user/deposit', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const userId = payload.userId ? parseInt(payload.userId) : null;
  const amount = payload.amount != null ? Number(payload.amount) : null;

  if (!userId || amount == null || Number.isNaN(amount) || amount <= 0) {
    return res.json(wrapResponse(null, 400, '缺少或无效的 userId 或 amount'));
  }

  const user = db.get('users').find({ id: userId }).value();
  if (!user) {
    return res.json(wrapResponse(null, 404, '用户不存在'));
  }

  const current = user.balance != null ? Number(user.balance) : 0;
  const newBalance = Number((current + amount).toFixed(2));

  db.get('users').find({ id: userId }).assign({ balance: newBalance }).write();

  res.json(wrapResponse({ userId, balance: newBalance }));
});

// ===================== 图书相关 =====================
server.get('/api/books', (req, res) => {
  const db = router.db;
  let books = db.get('books').value();
  
  const { keyword, category, page = 1, size = 10 } = req.query;
  
  if (keyword) {
    books = books.filter(b => 
      b.title.includes(keyword) || 
      b.author.includes(keyword) ||
      b.description.includes(keyword)
    );
  }
  
  if (category) {
    books = books.filter(b => b.category === category);
  }
  
  const total = books.length;
  const start = (page - 1) * size;
  const paginatedBooks = books.slice(start, start + parseInt(size));
  
  res.json(wrapResponse({
    list: paginatedBooks,
    total,
    page: parseInt(page),
    size: parseInt(size)
  }));
});

server.get('/api/books/search', (req, res) => {
  const db = router.db;
  let books = db.get('books').value();

  const { keyword = '', type, page = 1, size = 10 } = req.query;
  const q = (keyword || '').toLowerCase();

  if (q) {
    books = books.filter(b => {
      if (type === 'author') return (b.author || '').toLowerCase().includes(q);
      if (type === 'category') return (b.category || '').toLowerCase().includes(q);
      return (b.title || '').toLowerCase().includes(q)
        || (b.author || '').toLowerCase().includes(q)
        || (b.description || '').toLowerCase().includes(q);
    });
  }

  const total = books.length;
  const start = (parseInt(page) - 1) * parseInt(size);
  const pageBooks = books.slice(start, start + parseInt(size));

  const mapped = pageBooks.map(b => {
    // 计算平均评分
    const ratings = db.get('book_ratings').filter({ book_id: b.id }).value();
    const avgRating = ratings.length > 0 
      ? Number((ratings.reduce((s, r) => s + r.rating, 0) / ratings.length).toFixed(1))
      : 0;
    return {
      bookId: b.id,
      isbn: b.isbn,
      title: b.title,
      author: b.author,
      price: b.price,
      coverImage: b.cover_image,
      stockQuantity: b.stock_quantity,
      sellerId: b.seller_id,
      averageRating: avgRating
    };
  });

  res.json(wrapResponse(mapped));
});

server.get('/api/books/my-books', (req, res) => {
  const db = router.db;
  const userId = req.query.userId ? parseInt(req.query.userId) : null;
  let books = db.get('books').value();

  if (userId) {
    books = books.filter(b => b.seller_id === userId);
  }

  const mapped = books.map(b => ({
    id: b.id,
    title: b.title,
    stockQuantity: b.stock_quantity,
    stockStatus: (b.stock_quantity <= 0) ? 'Out of Stock' : (b.stock_quantity < 10 ? 'Low Stock' : 'In Stock')
  }));

  console.log('/api/books/my-books -> userId=', userId, 'matched=', mapped.length);

  res.json(wrapResponse(mapped));
});

server.get('/api/books/:id', (req, res) => {
  const db = router.db;
  const book = db.get('books').find({ id: parseInt(req.params.id) }).value();
  
  if (book) {
    // 计算平均评分
    const ratings = db.get('book_ratings').filter({ book_id: book.id }).value();
    const avgRating = ratings.length > 0 
      ? Number((ratings.reduce((s, r) => s + r.rating, 0) / ratings.length).toFixed(1))
      : 0;
    
    // 获取当前用户的评分 (myRating)
    const userId = req.query.userId ? parseInt(req.query.userId) : null;
    const myRatingRecord = userId 
      ? db.get('book_ratings').find({ book_id: book.id, user_id: userId }).value()
      : null;
    
    // 返回符合 API 规格的驼峰命名格式
    const response = {
      bookId: book.id,
      isbn: book.isbn,
      title: book.title,
      author: book.author,
      category: book.category,
      price: book.price,
      stockQuantity: book.stock_quantity,
      coverImage: book.cover_image,
      description: book.description,
      sellerId: book.seller_id,
      averageRating: avgRating,
      myRating: myRatingRecord ? myRatingRecord.rating : null
    };
    res.json(wrapResponse(response));
  } else {
    res.json(wrapResponse(null, 404, '图书不存在'));
  }
});

server.post('/api/books', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const newBook = {
    id: Date.now(),
    seller_id: payload.seller_id || payload.userId || 2,
    isbn: payload.isbn || `ISBN${Date.now()}`,
    title: payload.title || 'Untitled',
    author: payload.author || '',
    category: payload.category || 'General',
    price: payload.price || 0,
    stock_quantity: payload.stock_quantity || payload.stock || 0,
    cover_image: payload.cover_image || payload.cover || '',
    description: payload.description || ''
  };
  
  db.get('books').push(newBook).write();
  res.json(wrapResponse(newBook));
});

server.post('/api/books/add', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const newBook = {
    id: Date.now(),
    seller_id: payload.seller_id || payload.userId || 2,
    isbn: payload.isbn || `ISBN${Date.now()}`,
    title: payload.title || 'Untitled',
    author: payload.author || '',
    category: payload.category || 'General',
    price: payload.price || 0,
    stock_quantity: payload.stock_quantity || payload.stock || 0,
    cover_image: payload.cover_image || payload.cover || '',
    description: payload.description || ''
  };

  db.get('books').push(newBook).write();
  res.json(wrapResponse(newBook));
});

server.post('/api/books/update', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const id = payload.id ? parseInt(payload.id) : (payload.bookId ? parseInt(payload.bookId) : null);
  if (!id) {
    return res.json(wrapResponse(null, 400, '缺少 id'));
  }

  const existing = db.get('books').find({ id }).value();
  if (!existing) {
    return res.json(wrapResponse(null, 404, '图书不存在'));
  }

  const updates = {
    ...(payload.title && { title: payload.title }),
    ...(payload.author && { author: payload.author }),
    ...(payload.price != null && { price: payload.price }),
    ...(payload.stock_quantity != null && { stock_quantity: payload.stock_quantity }),
    ...(payload.stock != null && { stock_quantity: payload.stock }),
    ...(payload.cover_image && { cover_image: payload.cover_image }),
    ...(payload.cover && { cover_image: payload.cover }),
    ...(payload.description && { description: payload.description }),
    ...(payload.category && { category: payload.category })
  };

  db.get('books').find({ id }).assign(updates).write();
  const updated = db.get('books').find({ id }).value();
  res.json(wrapResponse(updated));
});

server.post('/api/books/delete', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const id = payload.id ? parseInt(payload.id) : (payload.bookId ? parseInt(payload.bookId) : null);
  if (!id) {
    return res.json(wrapResponse(null, 400, '缺少 id'));
  }

  const existing = db.get('books').find({ id }).value();
  if (!existing) {
    return res.json(wrapResponse(null, 404, '图书不存在'));
  }

  db.get('books').remove({ id }).write();
  res.json(wrapResponse(null, 200, '删除成功'));
});

server.post('/api/books/rate', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const bookId = payload.bookId ? parseInt(payload.bookId) : null;
  const rating = payload.rating != null ? Number(payload.rating) : null;
  const userId = payload.userId ? parseInt(payload.userId) : null;

  if (!bookId || rating == null) {
    return res.json(wrapResponse(null, 400, '缺少 bookId 或 rating'));
  }

  const book = db.get('books').find({ id: bookId }).value();
  if (!book) {
    return res.json(wrapResponse(null, 404, '图书不存在'));
  }

  // 保存到 book_ratings 表
  const existingRating = db.get('book_ratings').find({ user_id: userId, book_id: bookId }).value();
  
  if (existingRating) {
    db.get('book_ratings').find({ user_id: userId, book_id: bookId }).assign({ rating }).write();
  } else {
    db.get('book_ratings').push({
      id: Date.now(),
      user_id: userId,
      book_id: bookId,
      rating,
      create_time: new Date().toISOString().replace('T', ' ').substring(0, 19)
    }).write();
  }

  // 计算平均分
  const ratings = db.get('book_ratings').filter({ book_id: bookId }).value();
  const avg = ratings.reduce((s, r) => s + r.rating, 0) / ratings.length;
  const averageRating = Number(avg.toFixed(2));

  db.get('books').find({ id: bookId }).assign({ averageRating }).write();

  res.json(wrapResponse({ bookId, averageRating, ratingsCount: ratings.length }));
});

server.put('/api/books/:id', (req, res) => {
  const db = router.db;
  const id = parseInt(req.params.id);
  
  db.get('books').find({ id }).assign(req.body).write();
  const updatedBook = db.get('books').find({ id }).value();
  
  res.json(wrapResponse(updatedBook));
});

server.delete('/api/books/:id', (req, res) => {
  const db = router.db;
  db.get('books').remove({ id: parseInt(req.params.id) }).write();
  res.json(wrapResponse(null, 200, '删除成功'));
});

// ===================== 购物车相关 =====================
server.get('/api/cart', (req, res) => {
  const db = router.db;
  const cartItems = db.get('cart').value();
  
  const mapped = cartItems.map(ci => {
    const book = db.get('books').find({ id: ci.bookId }).value();
    return {
      bookId: ci.bookId,
      title: book?.title || '',
      coverImage: book?.cover_image || '',
      price: book?.price || 0,
      quantity: ci.quantity
    };
  });
  
  res.json(wrapResponse(mapped));
});

server.post('/api/cart', (req, res) => {
  const db = router.db;
  const { bookId, quantity = 1 } = req.body;
  
  const book = db.get('books').find({ id: parseInt(bookId) }).value();
  if (!book) {
    return res.json(wrapResponse(null, 404, '图书不存在'));
  }
  
  const existingItem = db.get('cart').find({ bookId: parseInt(bookId) }).value();
  if (existingItem) {
    db.get('cart').find({ bookId: parseInt(bookId) }).assign({ 
      quantity: existingItem.quantity + Number(quantity)
    }).write();
  } else {
    const newItem = {
      id: Date.now(),
      userId: 3,
      bookId: parseInt(bookId),
      quantity: Number(quantity)
    };
    db.get('cart').push(newItem).write();
  }
  
  res.json(wrapResponse(null, 200, '添加成功'));
});

server.post('/api/cart/add', (req, res) => {
  const db = router.db;
  const { bookId, quantity = 1, userId = 3 } = req.body;

  const book = db.get('books').find({ id: parseInt(bookId) }).value();
  if (!book) {
    return res.json(wrapResponse(null, 404, '图书不存在'));
  }

  const existingItem = db.get('cart').find({ bookId: parseInt(bookId) }).value();
  if (existingItem) {
    db.get('cart').find({ bookId: parseInt(bookId) }).assign({ 
      quantity: existingItem.quantity + Number(quantity)
    }).write();
  } else {
    const newItem = {
      id: Date.now(),
      userId,
      bookId: parseInt(bookId),
      quantity: Number(quantity)
    };
    db.get('cart').push(newItem).write();
  }

  res.json(wrapResponse(null, 200, '添加成功'));
});

server.post('/api/cart/update', (req, res) => {
  const db = router.db;
  const { bookId, quantity } = req.body;
  const item = db.get('cart').find({ bookId }).value();
  if (item) {
    db.get('cart').find({ bookId }).assign({ quantity }).write();
    return res.json(wrapResponse(null, 200, '更新成功'));
  }
  res.json(wrapResponse(null, 404, '购物车项不存在'));
});

server.post('/api/cart/delete', (req, res) => {
  const db = router.db;
  const { bookId } = req.body;
  db.get('cart').remove({ bookId }).write();
  res.json(wrapResponse(null, 200, '删除成功'));
});

server.delete('/api/cart', (req, res) => {
  const db = router.db;
  db.set('cart', []).write();
  res.json(wrapResponse(null, 200, '清空成功'));
});

server.post('/api/cart/clear', (req, res) => {
  const db = router.db;
  const userId = req.body && req.body.userId ? parseInt(req.body.userId) : 3;
  const others = db.get('cart').filter(ci => (ci.userId || 3) !== userId).value();
  db.set('cart', others).write();
  res.json(wrapResponse(null, 200, '清空成功'));
});

// ===================== 订单相关 =====================
server.get('/api/orders', (req, res) => {
  const db = router.db;
  const orders = db.get('orders').value();
  res.json(wrapResponse(orders));
});

server.get('/api/order/my-orders', (req, res) => {
  const db = router.db;
  const userId = parseInt(req.query.userId);
  if (!userId) {
    return res.json(wrapResponse([], 200, 'no userId provided'));
  }
  
  const orders = db.get('orders').filter({ user_id: userId }).value();
  const orderItems = db.get('order_items').value();

  const mapped = orders.map(o => {
    const items = orderItems.filter(oi => oi.order_id === o.id);
    return {
      orderId: `ORD${o.id}`,
      createTime: o.create_time || '',
      status: o.status || '',
      totalPrice: o.total_price || 0,
      bookCount: items.length
    };
  });

  res.json(wrapResponse(mapped));
});

server.get('/api/order/detail', (req, res) => {
  const db = router.db;
  const q = req.query.orderId;
  if (!q) {
    return res.json(wrapResponse([], 200, 'no orderId provided'));
  }

  // 支持 ORD1, ORD2 格式
  const idMatch = q.match(/^ORD(\d+)$/);
  const orderId = idMatch ? parseInt(idMatch[1]) : null;
  
  if (!orderId) {
    return res.json(wrapResponse([]));
  }

  const order = db.get('orders').find({ id: orderId }).value();

  if (!order) {
    return res.json(wrapResponse([]));
  }

  const orderItems = db.get('order_items').filter({ order_id: orderId }).value();
  
  const details = orderItems.map(it => {
    const book = db.get('books').find({ id: it.book_id }).value();
    return {
      bookId: it.book_id,
      bookTitle: book?.title || '',
      priceAtPurchase: it.price_at_purchase,
      quantity: it.quantity,
      itemSubTotal: it.price_at_purchase * it.quantity,
      status: order.status,
      createTime: order.create_time,
      recipientName: order.recipient_name,
      totalPrice: order.total_price
    };
  });

  res.json(wrapResponse(details));
});

server.post('/api/order/checkout', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const userId = payload.userId ? parseInt(payload.userId) : 3;
  const addressId = payload.addressId ? parseInt(payload.addressId) : null;

  if (!addressId) {
    return res.json(wrapResponse(null, 400, '缺少 addressId'));
  }

  const address = db.get('addresses').find({ id: addressId }).value();
  if (!address) {
    return res.json(wrapResponse(null, 404, '地址不存在'));
  }

  let sourceItems = Array.isArray(payload.items) ? payload.items : null;
  if (!sourceItems) {
    const cartItems = db.get('cart').filter(ci => (ci.userId || 3) === userId).value();
    sourceItems = cartItems.map(ci => ({ bookId: ci.bookId, quantity: ci.quantity }));
  }

  if (!sourceItems || sourceItems.length === 0) {
    return res.json(wrapResponse(null, 400, '购物车为空'));
  }

  let totalAmount = 0;
  const orderItemsData = [];
  
  sourceItems.forEach(item => {
    const book = db.get('books').find({ id: parseInt(item.bookId) }).value();
    if (book) {
      const qty = parseInt(item.quantity) || 1;
      const price = book.price;
      totalAmount += price * qty;
      orderItemsData.push({
        book_id: book.id,
        quantity: qty,
        price_at_purchase: price
      });
    }
  });

  // 校验余额
  const user = db.get('users').find({ id: userId }).value();
  const currentBalance = user && user.balance != null ? Number(user.balance) : 0;

  if (currentBalance < totalAmount) {
    return res.json(wrapResponse(null, 402, '余额不足'));
  }

  const newBalance = Number((currentBalance - totalAmount).toFixed(2));
  db.get('users').find({ id: userId }).assign({ balance: newBalance }).write();

  // 扣减库存
  orderItemsData.forEach(it => {
    const b = db.get('books').find({ id: parseInt(it.book_id) }).value();
    if (b) {
      const newStock = Math.max(0, (b.stock_quantity != null ? Number(b.stock_quantity) : 0) - it.quantity);
      db.get('books').find({ id: b.id }).assign({ stock_quantity: newStock }).write();
    }
  });

  const newOrder = {
    id: Date.now(),
    user_id: userId,
    total_price: totalAmount,
    recipient_name: address.recipient_name,
    recipient_phone: address.phone_number,
    shipping_address: address.address_detail,
    status: 'SUCCESS',
    create_time: new Date().toISOString().replace('T', ' ').substring(0, 19)
  };

  db.get('orders').push(newOrder).write();

  // 插入 order_items
  orderItemsData.forEach(oi => {
    db.get('order_items').push({
      id: Date.now() + Math.random(),
      order_id: newOrder.id,
      book_id: oi.book_id,
      quantity: oi.quantity,
      price_at_purchase: oi.price_at_purchase
    }).write();
  });

  // 清空购物车
  const others = db.get('cart').filter(ci => (ci.userId || 3) !== userId).value();
  db.set('cart', others).write();

  res.json(wrapResponse({
    orderId: `ORD${newOrder.id}`,
    totalPrice: newOrder.total_price,
    status: newOrder.status,
    balance: newBalance
  }));
});

server.post('/api/orders', (req, res) => {
  const db = router.db;
  const { addressId, items } = req.body;
  
  const address = db.get('addresses').find({ id: addressId }).value();
  
  let totalAmount = 0;
  const orderItemsData = [];
  
  items.forEach(item => {
    const book = db.get('books').find({ id: item.bookId }).value();
    totalAmount += book.price * item.quantity;
    orderItemsData.push({
      book_id: book.id,
      quantity: item.quantity,
      price_at_purchase: book.price
    });
  });
  
  const newOrder = {
    id: Date.now(),
    user_id: 3,
    total_price: totalAmount,
    recipient_name: address.recipient_name,
    recipient_phone: address.phone_number,
    shipping_address: address.address_detail,
    status: 'SUCCESS',
    create_time: new Date().toISOString().replace('T', ' ').substring(0, 19)
  };
  
  db.get('orders').push(newOrder).write();
  
  orderItemsData.forEach(oi => {
    db.get('order_items').push({
      id: Date.now() + Math.random(),
      order_id: newOrder.id,
      book_id: oi.book_id,
      quantity: oi.quantity,
      price_at_purchase: oi.price_at_purchase
    }).write();
  });
  
  // 清空购物车
  db.set('cart', []).write();
  
  res.json(wrapResponse(newOrder));
});

server.get('/api/admin/orders', (req, res) => {
  const db = router.db;
  const orders = db.get('orders').value();
  const orderItems = db.get('order_items').value();

  const rows = [];
  orders.forEach(o => {
    const items = orderItems.filter(oi => oi.order_id === o.id);
    items.forEach(it => {
      const book = db.get('books').find({ id: it.book_id }).value();
      rows.push({
        orderId: `ORD${o.id}`,
        bookTitle: book?.title || '',
        recipientName: o.recipient_name,
        status: o.status,
        itemSubTotal: it.price_at_purchase * it.quantity,
        totalPrice: o.total_price
      });
    });
    
    if (items.length === 0) {
      rows.push({
        orderId: `ORD${o.id}`,
        bookTitle: '',
        recipientName: o.recipient_name,
        status: o.status,
        itemSubTotal: 0,
        totalPrice: o.total_price
      });
    }
  });

  res.json(wrapResponse(rows));
});

// ===================== 地址相关 =====================
server.get('/api/addresses', (req, res) => {
  const db = router.db;
  const userId = req.query.userId ? parseInt(req.query.userId) : null;
  let addresses = db.get('addresses').value();

  if (userId) {
    addresses = addresses.filter(a => a.user_id === userId);
  }

  const mapped = addresses.map(a => ({
    id: a.id,
    recipientName: a.recipient_name,
    phoneNumber: a.phone_number,
    addressDetail: a.address_detail
  }));

  res.json(wrapResponse(mapped));
});

server.post('/api/addresses/add', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const newAddress = {
    id: Date.now(),
    user_id: payload.userId || 3,
    recipient_name: payload.recipientName || payload.recipient_name || '',
    phone_number: payload.phoneNumber || payload.phone_number || '',
    address_detail: payload.addressDetail || payload.address_detail || '',
    create_time: new Date().toISOString().replace('T', ' ').substring(0, 19)
  };

  db.get('addresses').push(newAddress).write();

  const out = {
    id: newAddress.id,
    recipientName: newAddress.recipient_name,
    phoneNumber: newAddress.phone_number,
    addressDetail: newAddress.address_detail
  };

  res.json(wrapResponse(out));
});

server.post('/api/addresses/update', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const id = payload.id ? parseInt(payload.id) : null;
  if (!id) return res.json(wrapResponse(null, 400, '缺少 id'));

  const updates = {
    ...(payload.recipientName && { recipient_name: payload.recipientName }),
    ...(payload.phoneNumber && { phone_number: payload.phoneNumber }),
    ...(payload.addressDetail && { address_detail: payload.addressDetail })
  };

  db.get('addresses').find({ id }).assign(updates).write();
  const a = db.get('addresses').find({ id }).value();
  const out = {
    id: a.id,
    recipientName: a.recipient_name,
    phoneNumber: a.phone_number,
    addressDetail: a.address_detail
  };

  res.json(wrapResponse(out));
});

server.post('/api/addresses/delete', (req, res) => {
  const db = router.db;
  const id = req.body && req.body.id ? parseInt(req.body.id) : null;
  if (!id) return res.json(wrapResponse(null, 400, '缺少 id'));
  db.get('addresses').remove({ id }).write();
  res.json(wrapResponse(null, 200, '删除成功'));
});

server.post('/api/addresses', (req, res) => {
  const db = router.db;
  const payload = req.body || {};
  const newAddress = {
    id: Date.now(),
    user_id: 3,
    recipient_name: payload.recipientName || payload.recipient_name || '',
    phone_number: payload.phoneNumber || payload.phone_number || '',
    address_detail: payload.addressDetail || payload.address_detail || '',
    create_time: new Date().toISOString().replace('T', ' ').substring(0, 19)
  };
  
  db.get('addresses').push(newAddress).write();
  res.json(wrapResponse(newAddress));
});

server.delete('/api/addresses/:id', (req, res) => {
  const db = router.db;
  db.get('addresses').remove({ id: parseInt(req.params.id) }).write();
  res.json(wrapResponse(null, 200, '删除成功'));
});

// ===================== 用户管理 (管理员) =====================
server.get('/api/admin/users', (req, res) => {
  const db = router.db;
  const users = db.get('users').value().map(u => {
    const { password, ...userWithoutPassword } = u;
    return userWithoutPassword;
  });
  res.json(wrapResponse(users));
});

server.put('/api/admin/users/:id', (req, res) => {
  const db = router.db;
  const id = parseInt(req.params.id);
  
  db.get('users').find({ id }).assign(req.body).write();
  const updated = db.get('users').find({ id }).value();
  const { password, ...userWithoutPassword } = updated;
  
  res.json(wrapResponse(userWithoutPassword));
});

server.delete('/api/admin/users/:id', (req, res) => {
  const db = router.db;
  db.get('users').remove({ id: parseInt(req.params.id) }).write();
  res.json(wrapResponse(null, 200, '删除成功'));
});

// 启动服务器
const PORT = 8080;
server.listen(PORT, () => {
  console.log(`
  ╔══════════════════════════════════════════════════════════╗
  ║                                                          ║
  ║   📚 Online Bookstore Mock Server (SQL Aligned)          ║
  ║                                                          ║
  ║   Server running at: http://localhost:${PORT}               ║
  ║                                                          ║
  ║   字段名已与 SQL 表对齐 ✓                                   ║
  ║                                                          ║
  ╚══════════════════════════════════════════════════════════╝
  `);
});
