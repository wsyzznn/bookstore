package com.example.onlinebookstorebackend.repository;

import com.example.onlinebookstorebackend.entity.view.VOrderDetail;
import com.example.onlinebookstorebackend.entity.view.VOrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VOrderDetailRepository extends JpaRepository<VOrderDetail, VOrderDetailId> {

    // 查询某个商家的所有订单
    @Query("SELECT DISTINCT v FROM VOrderDetail v WHERE v.sellerId = :sellerId")
    List<VOrderDetail> findBySellerId(@Param("sellerId") Long sellerId);

    // 查询所有订单（去重）
    @Query("SELECT DISTINCT v FROM VOrderDetail v")
    List<VOrderDetail> findAllDistinct();

    // 按订单ID查询
    List<VOrderDetail> findByOrderId(Long orderId);

    // 按订单ID和书籍ID查询
    VOrderDetail findByOrderIdAndBookId(Long orderId, Long bookId);
}