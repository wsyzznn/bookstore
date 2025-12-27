package com.example.onlinebookstorebackend.dto;

import lombok.Data;

@Data
public class BookManageDTO {
    private Long id;
    private String title;
    private Integer stockQuantity;
    private String stockStatus;  // "In Stock", "Low Stock", "Out of Stock"

    // 删除 price 字段
    // private BigDecimal price;  ← 注释掉或删除这行

    // 根据库存计算状态
    public void calculateStockStatus() {
        if (stockQuantity == null || stockQuantity == 0) {
            stockStatus = "Out of Stock";
        } else if (stockQuantity < 5) {
            stockStatus = "Low Stock";
        } else {
            stockStatus = "In Stock";
        }
    }
}