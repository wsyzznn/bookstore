package com.example.onlinebookstorebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String msg;
    private T data;

    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(200, msg, data);
    }

    public static ApiResponse<Void> success(String msg) {
        return new ApiResponse<>(200, msg, null);
    }

    // 专门处理字符串数据的方法
    public static ApiResponse<String> successString(String data) {
        return new ApiResponse<>(200, "success", data);
    }

    // 错误响应 - 泛型版本
    public static <T> ApiResponse<T> error(Integer code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }

    // 错误响应 - Void版本（保持向后兼容）
    public static ApiResponse<Void> errorVoid(Integer code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }
}