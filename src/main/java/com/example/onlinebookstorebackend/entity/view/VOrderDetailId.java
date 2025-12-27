package com.example.onlinebookstorebackend.entity.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VOrderDetailId implements Serializable {
    private Long orderId;
    private Long bookId;
}