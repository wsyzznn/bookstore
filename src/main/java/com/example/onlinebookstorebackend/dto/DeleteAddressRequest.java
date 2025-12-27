package com.example.onlinebookstorebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteAddressRequest {
    @NotNull(message = "地址ID不能为空")
    private Long id;
}