package com.example.onlinebookstorebackend.service;

import com.example.onlinebookstorebackend.common.BusinessException;
import com.example.onlinebookstorebackend.dto.BookDTO;
import com.example.onlinebookstorebackend.dto.BookManageDTO;
import com.example.onlinebookstorebackend.entity.Book;
import com.example.onlinebookstorebackend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookManageService {

    @Autowired
    private BookRepository bookRepository;

    /**
     * 添加书籍
     */
    @Transactional
    public void addBook(BookDTO dto) {
        // 验证ISBN是否已存在
        Optional<Book> existingBook = bookRepository.findByIsbn(dto.getIsbn());
        if (existingBook.isPresent()) {
            throw new BusinessException("ISBN已存在: " + dto.getIsbn());
        }

        // 验证卖家ID
        if (dto.getSellerId() == null) {
            throw new BusinessException("卖家ID不能为空");
        }

        // 创建新书籍
        Book book = new Book();
        book.setSellerId(dto.getSellerId());
        book.setIsbn(dto.getIsbn().trim());
        book.setTitle(dto.getTitle().trim());
        book.setAuthor(dto.getAuthor().trim());
        book.setCategory(dto.getCategory() != null ? dto.getCategory().trim() : "General");
        book.setPrice(dto.getPrice());
        book.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        book.setCoverImage(dto.getCoverImage());
        book.setDescription(dto.getDescription());

        bookRepository.save(book);
    }

    /**
     * 更新书籍 - 支持部分字段更新（符合开发文档）
     */
    @Transactional
    public void updateBook(BookDTO dto, Long sellerId) {
        // 验证书籍ID
        if (dto.getId() == null) {
            throw new BusinessException("书籍ID不能为空");
        }

        // 查找书籍
        Book book = bookRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("书籍不存在"));

        // 验证权限：只能修改自己的书籍
        if (!book.getSellerId().equals(sellerId)) {
            throw new BusinessException(403, "无权修改该书籍");
        }

        // 如果有提供ISBN，检查是否重复
        if (dto.getIsbn() != null && !dto.getIsbn().trim().isEmpty()) {
            String newIsbn = dto.getIsbn().trim();
            if (!book.getIsbn().equals(newIsbn)) {
                Optional<Book> existingBook = bookRepository.findByIsbn(newIsbn);
                if (existingBook.isPresent() && !existingBook.get().getId().equals(dto.getId())) {
                    throw new BusinessException("ISBN已存在: " + newIsbn);
                }
                book.setIsbn(newIsbn);
            }
        }

        // 部分更新：只有非空且非空字符串的字段才更新
        // 符合开发文档示例：只传递需要更新的字段
        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            book.setTitle(dto.getTitle().trim());
        }

        if (dto.getAuthor() != null && !dto.getAuthor().trim().isEmpty()) {
            book.setAuthor(dto.getAuthor().trim());
        }

        if (dto.getCategory() != null && !dto.getCategory().trim().isEmpty()) {
            book.setCategory(dto.getCategory().trim());
        }

        if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            book.setPrice(dto.getPrice());
        }

        if (dto.getStockQuantity() != null && dto.getStockQuantity() >= 0) {
            book.setStockQuantity(dto.getStockQuantity());
        }

        if (dto.getCoverImage() != null) {
            book.setCoverImage(dto.getCoverImage());
        }

        if (dto.getDescription() != null) {
            book.setDescription(dto.getDescription());
        }

        bookRepository.save(book);
    }

    /**
     * 删除书籍
     */
    @Transactional
    public void deleteBook(Long bookId, Long sellerId) {
        if (bookId == null) {
            throw new BusinessException("书籍ID不能为空");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("书籍不存在"));

        // 验证权限：只能删除自己的书籍
        if (!book.getSellerId().equals(sellerId)) {
            throw new BusinessException(403, "无权删除该书籍");
        }

        bookRepository.delete(book);
    }

    /**
     * 获取我的书籍列表
     */
    public List<BookManageDTO> getMyManageBooks(Long sellerId) {
        List<Book> books = bookRepository.findBySellerId(sellerId);

        return books.stream()
                .map(book -> {
                    BookManageDTO dto = new BookManageDTO();
                    dto.setId(book.getId());
                    dto.setTitle(book.getTitle());
                    dto.setStockQuantity(book.getStockQuantity());
                    // 不设置 price
                    dto.calculateStockStatus();  // 计算库存状态
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 实体转DTO
     */
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setSellerId(book.getSellerId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setCategory(book.getCategory());
        dto.setPrice(book.getPrice());
        dto.setStockQuantity(book.getStockQuantity());
        dto.setCoverImage(book.getCoverImage());
        dto.setDescription(book.getDescription());
        return dto;
    }
}