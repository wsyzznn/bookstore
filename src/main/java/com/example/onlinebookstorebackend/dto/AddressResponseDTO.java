// AddressResponseDTO.java - 专门用于响应，不包含userId和fullAddress
package com.example.onlinebookstorebackend.dto;

import lombok.Data;

@Data
public class AddressResponseDTO {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String addressDetail;
    // 注意：根据开发文档，响应中不应该有userId和fullAddress
}

