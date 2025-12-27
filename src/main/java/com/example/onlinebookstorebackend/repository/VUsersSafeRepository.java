package com.example.onlinebookstorebackend.repository;

import com.example.onlinebookstorebackend.entity.view.VUsersSafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VUsersSafeRepository extends JpaRepository<VUsersSafe, Long> {
}