package com.example.onlinebookstorebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserDTO {
    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;
}