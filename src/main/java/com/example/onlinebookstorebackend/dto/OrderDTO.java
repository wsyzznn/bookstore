package com.example.onlinebookstorebackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private BigDecimal itemSubTotal;
    private String status;
    private String bookTitle;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private Long sellerId;
    private LocalDateTime createTime;
}