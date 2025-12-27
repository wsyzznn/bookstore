package com.example.onlinebookstorebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度必须在6-30个字符之间")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String role = "CUSTOMER"; // CUSTOMER, MERCHANT, ADMIN

    // 验证角色是否合法
    public boolean isValidRole() {
        return "CUSTOMER".equals(role) ||
                "MERCHANT".equals(role) ||
                "ADMIN".equals(role);
    }
}