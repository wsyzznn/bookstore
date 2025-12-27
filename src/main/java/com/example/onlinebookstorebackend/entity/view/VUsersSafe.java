package com.example.onlinebookstorebackend.entity.view;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "v_users_safe")
@Data
public class VUsersSafe {
    @Id
    private Long id;

    private String username;

    private String role;

    private String email;
}