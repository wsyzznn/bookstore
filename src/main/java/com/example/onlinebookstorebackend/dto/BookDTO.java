package com.example.onlinebookstorebackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookDTO {
    private Long id;
    private Long sellerId;
    private String isbn;
    private String title;
    private String author;
    private String category = "General";
    private BigDecimal price;
    private Integer stockQuantity = 100;
    private String coverImage;
    private String description;

    // 计算小计
    public BigDecimal getSubtotal(Integer quantity) {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}