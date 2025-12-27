package com.example.onlinebookstorebackend.service;

import com.example.onlinebookstorebackend.common.BusinessException;
import com.example.onlinebookstorebackend.dto.UpdateUserDTO;
import com.example.onlinebookstorebackend.dto.UserDTO;
import com.example.onlinebookstorebackend.entity.User;
import com.example.onlinebookstorebackend.entity.view.VOrderDetail;
import com.example.onlinebookstorebackend.entity.view.VUsersSafe;
import com.example.onlinebookstorebackend.repository.UserRepository;
import com.example.onlinebookstorebackend.repository.VOrderDetailRepository;
import com.example.onlinebookstorebackend.repository.VUsersSafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private VUsersSafeRepository vUsersSafeRepository;

    @Autowired
    private VOrderDetailRepository vOrderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 获取所有用户（使用v_users_safe视图脱敏）
     */
    public List<UserDTO> getAllUsers() {
        List<VUsersSafe> safeUsers = vUsersSafeRepository.findAll();
        return safeUsers.stream()
                .map(this::convertFromView)
                .collect(Collectors.toList());
    }

    /**
     * 按角色查询用户
     */
    public List<UserDTO> getUsersByRole(String role) {
        // 从视图中过滤
        List<VUsersSafe> allUsers = vUsersSafeRepository.findAll();
        return allUsers.stream()
                .filter(user -> role.equals(user.getRole()))
                .map(this::convertFromView)
                .collect(Collectors.toList());
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public void updateUser(UpdateUserDTO dto) {
        // 验证用户ID
        if (dto.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 查找用户（使用原始表）
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + dto.getId()));

        // 更新邮箱（如果提供了）
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            String newEmail = dto.getEmail().trim();

            // 检查邮箱是否被其他用户使用
            Optional<User> existingUser = userRepository.findByEmail(newEmail);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(dto.getId())) {
                throw new BusinessException("邮箱已被其他用户使用: " + newEmail);
            }

            user.setEmail(newEmail);
        }

        // 更新密码（如果提供了）
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            String rawPassword = dto.getPassword().trim();

            // 密码长度验证
            if (rawPassword.length() < 6) {
                throw new BusinessException("密码长度不能少于6位");
            }

            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
        }

        userRepository.save(user);
    }

    /**
     * 获取所有订单（使用v_order_detail视图）
     */
    public List<VOrderDetail> getAllOrders(Long currentUserId, String currentUserRole) {
        System.out.println("=== AdminService.getAllOrders 开始 ===");
        System.out.println("参数: userId=" + currentUserId + ", role=" + currentUserRole);

        List<VOrderDetail> result;
        if ("ADMIN".equals(currentUserRole)) {
            System.out.println("角色是ADMIN，查询所有订单");
            // 使用去重查询
            result = vOrderDetailRepository.findAllDistinct();
            System.out.println("查询到 " + result.size() + " 条记录");
        } else if ("MERCHANT".equals(currentUserRole)) {
            System.out.println("角色是MERCHANT，查询sellerId=" + currentUserId + "的订单");
            result = vOrderDetailRepository.findBySellerId(currentUserId);
            System.out.println("查询到 " + result.size() + " 条记录");
        } else {
            throw new BusinessException(403, "无权查看订单");
        }

        // 打印每条记录
        for (int i = 0; i < result.size(); i++) {
            VOrderDetail order = result.get(i);
            System.out.println(i + ": orderId=" + order.getOrderId() +
                    ", bookId=" + order.getBookId() +
                    ", title=" + order.getBookTitle());
        }

        // 验证去重效果
        Set<String> uniqueKeys = result.stream()
                .map(order -> order.getOrderId() + "-" + order.getBookId())
                .collect(Collectors.toSet());
        System.out.println("去重后唯一记录数: " + uniqueKeys.size());

        System.out.println("=== AdminService.getAllOrders 结束 ===");
        return result;
    }

    /**
     * 视图实体转DTO
     */
    private UserDTO convertFromView(VUsersSafe view) {
        UserDTO dto = new UserDTO();
        dto.setId(view.getId());
        dto.setUsername(view.getUsername());
        dto.setEmail(view.getEmail());
        dto.setRole(view.getRole());
        return dto;
    }
}