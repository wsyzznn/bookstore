package com.example.onlinebookstorebackend.repository;

import com.example.onlinebookstorebackend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findBySellerId(Long sellerId);

    Optional<Book> findByIsbn(String isbn);

    // 检查ISBN是否已存在（排除自身）
    @Query("SELECT COUNT(b) > 0 FROM Book b WHERE b.isbn = :isbn AND b.id != :id")
    boolean existsByIsbnAndIdNot(@Param("isbn") String isbn, @Param("id") Long id);

    // 搜索功能
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByIsbnContaining(String isbn);

    // 按分类查询
    List<Book> findByCategory(String category);

    // 按价格范围查询
    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}