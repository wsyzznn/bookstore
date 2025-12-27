package com.example.onlinebookstorebackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address_detail", nullable = false, length = 255)
    private String addressDetail;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    // 地址验证
    public boolean isValid() {
        return recipientName != null && !recipientName.trim().isEmpty() &&
                phoneNumber != null && !phoneNumber.trim().isEmpty() &&
                addressDetail != null && !addressDetail.trim().isEmpty();
    }
}