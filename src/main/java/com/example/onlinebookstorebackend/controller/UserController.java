package com.example.onlinebookstorebackend.controller;

import com.example.onlinebookstorebackend.dto.*;
import com.example.onlinebookstorebackend.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册 - POST /api/user/register
     */
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterDTO dto) {
        userService.register(dto);
        return ApiResponse.success("注册成功");
    }

    /**
     * 用户登录 - POST /api/user/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO dto,
            HttpSession session) {
        LoginResponseDTO result = userService.login(dto);
        return ApiResponse.success("登录成功", result);
    }

    /**
     * 用户退出 - POST /api/user/logout
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        userService.logout();
        return ApiResponse.success("退出成功");
    }

    /**
     * 余额充值 - POST /api/user/deposit
     */
    @PostMapping("/deposit")
    public ApiResponse<BigDecimal> deposit(
            @RequestBody @Valid DepositRequest request,
            HttpSession session) {
        BigDecimal balance = userService.deposit(request.getUserId(), request.getAmount());
        return ApiResponse.success("充值成功", balance);
    }

    /**
     * 测试接口 - GET /api/user/test
     */
    @GetMapping("/test")
    public ApiResponse<String> test(HttpSession session) {
        String result = userService.test();
        return ApiResponse.success("测试成功", result);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        System.out.println("收到密码重置请求: " + request.getEmail());

        try {
            userService.resetPassword(request.getEmail());
            return ApiResponse.success("重置邮件已发送");
        } catch (Exception e) {
            // 即使出错也返回成功（安全考虑，防止邮箱探测）
            System.out.println("密码重置处理异常: " + e.getMessage());
            return ApiResponse.success("重置邮件已发送");
        }
    }
    /**
     * 公开接口（不需要登录）- GET /api/user/public
     */
    @GetMapping("/public")
    public ApiResponse<String> publicEndpoint() {
        return ApiResponse.success("公开接口", "This is a public endpoint");
    }
}