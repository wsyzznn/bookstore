package com.example.onlinebookstorebackend.controller;

import com.example.onlinebookstorebackend.common.BusinessException;
import com.example.onlinebookstorebackend.dto.ApiResponse;
import com.example.onlinebookstorebackend.dto.UpdateUserDTO;
import com.example.onlinebookstorebackend.dto.UserDTO;
import com.example.onlinebookstorebackend.entity.view.VOrderDetail;
import com.example.onlinebookstorebackend.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 检查是否是管理员
     */
    private void checkAdmin(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (role == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(403, "仅管理员可访问");
        }
    }

    /**
     * 获取所有用户列表 - GET /api/admin/users
     * 必须使用v_users_safe视图（开发文档规定）
     */
    @GetMapping("/users")
    public ApiResponse<List<UserDTO>> allUsers(HttpSession session) {
        checkAdmin(session);
        List<UserDTO> users = adminService.getAllUsers();
        return ApiResponse.success(users);
    }

    /**
     * 按角色获取用户 - GET /api/admin/users/role?role=CUSTOMER
     */
    @GetMapping("/users/role")
    public ApiResponse<List<UserDTO>> usersByRole(
            @RequestParam String role,
            HttpSession session) {
        checkAdmin(session);
        List<UserDTO> users = adminService.getUsersByRole(role);
        return ApiResponse.success(users);
    }

    /**
     * 修改用户信息 - POST /api/admin/users/update
     * 开发文档示例：
     * {
     *   "id": 1001,
     *   "password": "newPassword123",
     *   "email": "new@t.com"
     * }
     */
    @PostMapping("/users/update")
    public ApiResponse<Void> updateUser(
            @RequestBody UpdateUserDTO dto,
            HttpSession session) {

        checkAdmin(session);
        adminService.updateUser(dto);
        return ApiResponse.success("用户信息已更新");
    }

    /**
     * 系统订单管理 - GET /api/admin/orders
     * 权限: ADMIN (看所有) / MERCHANT (看相关 - 通过视图中的 sellerId 过滤)
     * 开发文档响应格式：
     * {
     *   "orderId": 20230001,
     *   "totalPrice": 256.00,
     *   "itemSubTotal": 128.00,
     *   "status": "SUCCESS",
     *   "bookTitle": "深入理解计算机系统",
     *   "recipientName": "张三",
     *   "sellerId": 2
     * }
     */
    // ✅ 正确：方法签名和返回值类型一致
    @GetMapping("/orders")
    public ApiResponse<List<Map<String, Object>>> getAllOrders(HttpSession session) {
        // 检查登录状态
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        if (userId == null || userRole == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 只允许管理员和商家访问
        if (!"ADMIN".equals(userRole) && !"MERCHANT".equals(userRole)) {
            throw new BusinessException(403, "仅管理员和商家可查看订单");
        }

        // 获取原始数据
        List<VOrderDetail> orders = adminService.getAllOrders(userId, userRole);

        // 转换为Map列表
        List<Map<String, Object>> result = orders.stream()
                .map(order -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("orderId", order.getOrderId());
                    map.put("totalPrice", order.getTotalPrice());
                    map.put("itemSubTotal", order.getItemSubTotal());
                    map.put("status", order.getStatus());
                    map.put("bookTitle", order.getBookTitle());
                    map.put("recipientName", order.getRecipientName());
                    map.put("sellerId", order.getSellerId());
                    return map;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    /**
     * 测试接口 - GET /api/admin/test
     */
    @GetMapping("/test")
    public ApiResponse<Void> testAdmin(HttpSession session) {
        checkAdmin(session);
        return ApiResponse.success("管理员接口正常");
    }
}