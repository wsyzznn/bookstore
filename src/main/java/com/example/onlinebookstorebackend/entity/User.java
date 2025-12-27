package com.example.onlinebookstorebackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 100)
    private String email;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance = new BigDecimal("99999.00"); // 默认余额

    @Column(nullable = false, length = 20)
    private String role = "CUSTOMER";

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}