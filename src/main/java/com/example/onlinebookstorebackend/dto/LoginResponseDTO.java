package com.example.onlinebookstorebackend.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatar = null;  // 添加avatar字段
    private String token;

    // 可以添加一个方法格式化token
    public void setSessionId(String sessionId) {
        this.token = "JSESSIONID-" + sessionId.substring(0, 8);  // 示例格式
    }
}
