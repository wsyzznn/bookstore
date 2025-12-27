// AddressAddDTO.java - 专门用于新增地址请求
package com.example.onlinebookstorebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressAddDTO {
    @NotBlank(message = "收件人姓名不能为空")
    private String recipientName;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @NotBlank(message = "详细地址不能为空")
    private String addressDetail;

    // 注意：根据开发文档示例有userId，但实际不应该由前端传递
}