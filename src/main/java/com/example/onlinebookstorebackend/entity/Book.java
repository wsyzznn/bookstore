package com.example.onlinebookstorebackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn")
})
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(length = 50)
    private String category = "General";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 100;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    // 检查库存是否足够
    public boolean hasEnoughStock(Integer quantity) {
        return stockQuantity >= quantity;
    }

    // 减少库存
    public void reduceStock(Integer quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new RuntimeException("库存不足，当前库存: " + stockQuantity + ", 需求: " + quantity);
        }
        stockQuantity -= quantity;
    }

    // 增加库存
    public void increaseStock(Integer quantity) {
        if (quantity < 0) {
            throw new RuntimeException("增加数量不能为负数");
        }
        stockQuantity += quantity;
    }

    // 验证书籍信息
    public boolean isValid() {
        return isbn != null && !isbn.trim().isEmpty() &&
                title != null && !title.trim().isEmpty() &&
                author != null && !author.trim().isEmpty() &&
                price != null && price.compareTo(BigDecimal.ZERO) > 0 &&
                stockQuantity != null && stockQuantity >= 0;
    }
}