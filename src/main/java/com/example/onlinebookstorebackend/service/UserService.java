package com.example.onlinebookstorebackend.service;

import com.example.onlinebookstorebackend.common.BusinessException;
import com.example.onlinebookstorebackend.dto.*;
import com.example.onlinebookstorebackend.entity.User;
import com.example.onlinebookstorebackend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    // 改为注入的方式
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterDTO dto) {
        // 验证用户名
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 验证邮箱
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 验证角色
        String role = dto.getRole().toUpperCase();
        if (!"CUSTOMER".equals(role) && !"MERCHANT".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException("角色无效，只能是CUSTOMER、MERCHANT或ADMIN");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // 自动加密
        user.setEmail(dto.getEmail());
        user.setRole(role);

        userRepository.save(user);
        System.out.println("✅ 用户注册成功: " + dto.getUsername() + "，密码已自动加密存储");
    }

    @Transactional
    public void resetPassword(String email) {
        System.out.println("=== 密码重置请求 ===");
        System.out.println("请求邮箱: " + email);

        // 检查邮箱是否存在
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // 出于安全考虑，即使邮箱不存在也返回成功
            System.out.println("邮箱不存在，但仍返回成功（安全考虑）");
            return;
        }

        User user = userOpt.get();
        System.out.println("找到用户: " + user.getUsername());

        // 模拟发送重置邮件
        System.out.println("模拟发送重置密码邮件到: " + email);
        System.out.println("邮件内容：请点击链接重置密码");

        // 实际项目中这里应该：
        // 1. 生成重置token
        // 2. 保存到数据库（设置过期时间）
        // 3. 发送包含重置链接的邮件

        System.out.println("密码重置流程完成（模拟）");
    }
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // 1. 查找用户
        Optional<User> userOpt = userRepository.findByUsername(dto.getUsername());
        if (userOpt.isEmpty()) {
            throw new BusinessException("用户名或密码错误");
        }

        User user = userOpt.get();
        String storedPassword = user.getPassword();

        System.out.println("=== 登录调试信息 ===");
        System.out.println("用户名: " + dto.getUsername());
        System.out.println("输入密码: " + dto.getPassword());
        System.out.println("存储密码格式: " +
                (storedPassword.startsWith("$2a$") ? "BCrypt加密" : "明文"));

        boolean passwordValid = false;

        // 2. 智能密码验证
        if (storedPassword.startsWith("$2a$")) {
            // 已经是BCrypt格式，正常验证
            passwordValid = passwordEncoder.matches(dto.getPassword(), storedPassword);
            System.out.println("BCrypt验证结果: " + passwordValid);
        } else {
            // 是明文格式，直接比较
            passwordValid = storedPassword.equals(dto.getPassword());
            System.out.println("明文验证结果: " + passwordValid);

            // 3. 如果密码正确，自动升级为BCrypt格式
            if (passwordValid) {
                String encodedPassword = passwordEncoder.encode(dto.getPassword());
                user.setPassword(encodedPassword);
                userRepository.save(user);
                System.out.println("⚠️ 安全升级: 用户 " + user.getUsername() + " 的密码已从明文升级为BCrypt加密格式");
            }
        }

        if (!passwordValid) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 登录成功，把信息放入 Session
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("username", user.getUsername());

        // 5. 构建返回 DTO
        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(session.getId());

        System.out.println("✅ 登录成功: userId=" + user.getId() + ", role=" + user.getRole());

        return response;
    }

    public void logout() {
        session.invalidate();
    }

    @Transactional
    public BigDecimal deposit(Long userId, BigDecimal amount) {
        // 1. 参数验证
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        // 2. 验证当前用户权限（防越权）
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null || !currentUserId.equals(userId)) {
            throw new BusinessException(403, "只能给自己充值");
        }

        // 3. 查找用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 4. 更新余额
        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.add(amount);
        user.setBalance(newBalance);

        userRepository.save(user);

        System.out.println("✅ 充值成功: 用户 " + user.getUsername() + " 余额从 " +
                currentBalance + " 增加到 " + newBalance);

        return newBalance;
    }

    // 测试方法
    public String test() {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        if (userId != null) {
            return "用户ID: " + userId + ", 角色: " + userRole + ", Session: " + session.getId();
        }
        return "未登录，当前Session: " + session.getId();
    }
}