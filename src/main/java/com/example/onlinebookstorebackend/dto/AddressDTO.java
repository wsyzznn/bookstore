package com.example.onlinebookstorebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private Long userId;  // 内部使用，不返回给前端

    @NotBlank(message = "收件人姓名不能为空")
    private String recipientName;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @NotBlank(message = "详细地址不能为空")
    private String addressDetail;

    // 移除getFullAddress()方法，因为响应中不需要
}