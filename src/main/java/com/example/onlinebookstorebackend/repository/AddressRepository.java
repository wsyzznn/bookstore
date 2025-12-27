package com.example.onlinebookstorebackend.repository;

import com.example.onlinebookstorebackend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    // 返回受影响行数，用于判断是否成功删除
    int deleteByIdAndUserId(Long id, Long userId);
}
