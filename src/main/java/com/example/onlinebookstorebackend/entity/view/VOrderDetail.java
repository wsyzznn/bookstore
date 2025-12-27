package com.example.onlinebookstorebackend.entity.view;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "v_order_detail")
@IdClass(VOrderDetailId.class)  // 指定使用复合主键
@Data
public class VOrderDetail {
    @Id
    @Column(name = "orderid")
    private Long orderId;

    @Id  // 这也是主键的一部分
    @Column(name = "bookid")
    private Long bookId;

    @Column(name = "userid")
    private Long userId;

    @Column(name = "createtime")
    private LocalDateTime createTime;

    @Column(name = "totalprice")
    private BigDecimal totalPrice;

    private String status;

    @Column(name = "recipientname")
    private String recipientName;

    @Column(name = "recipientphone")
    private String recipientPhone;

    @Column(name = "shippingaddress")
    private String shippingAddress;

    @Column(name = "booktitle")
    private String bookTitle;

    private Integer quantity;

    @Column(name = "priceatpurchase")
    private BigDecimal priceAtPurchase;

    @Column(name = "itemsubtotal")
    private BigDecimal itemSubTotal;

    @Column(name = "bookcover")
    private String bookCover;

    @Column(name = "sellerid")
    private Long sellerId;

    // 可以添加equals和hashCode方法确保唯一性
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOrderDetail that = (VOrderDetail) o;
        return orderId.equals(that.orderId) && bookId.equals(that.bookId);
    }

    @Override
    public int hashCode() {
        return 31 * orderId.hashCode() + bookId.hashCode();
    }
}