package com.example.onlinebookstorebackend.controller;

import com.example.onlinebookstorebackend.common.BusinessException;
import com.example.onlinebookstorebackend.dto.ApiResponse;
import com.example.onlinebookstorebackend.dto.BookDTO;
import com.example.onlinebookstorebackend.dto.BookManageDTO;
import com.example.onlinebookstorebackend.service.BookManageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookManageController {

    @Autowired
    private BookManageService bookManageService;

    /**
     * 检查是否是商家或管理员
     */
    private void checkMerchant(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"MERCHANT".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException(403, "仅商家或管理员可操作");
        }
    }

    /**
     * 上架新书 - POST /api/books/add
     */
    @PostMapping("/add")
    public ApiResponse<Void> add(
            @RequestBody BookDTO dto,
            HttpSession session) {
        checkMerchant(session);
        Long sellerId = (Long) session.getAttribute("userId");
        dto.setSellerId(sellerId);
        bookManageService.addBook(dto);
        return ApiResponse.success("上架成功");
    }

    /**
     * 修改书籍 - POST /api/books/update
     */
    @PostMapping("/update")
    public ApiResponse<Void> update(
            @RequestBody BookDTO dto,
            HttpSession session) {
        checkMerchant(session);
        Long sellerId = (Long) session.getAttribute("userId");
        bookManageService.updateBook(dto, sellerId);
        return ApiResponse.success("修改成功");
    }

    /**
     * 删除书籍 - POST /api/books/delete
     * 开发文档示例：{ "id": 1 }
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(
            @RequestBody DeleteBookRequest request,
            HttpSession session) {
        checkMerchant(session);
        Long sellerId = (Long) session.getAttribute("userId");
        bookManageService.deleteBook(request.getId(), sellerId);
        return ApiResponse.success("删除成功");
    }

    /**
     * 获取我的书籍列表 - GET /api/books/my-books
     */
    @GetMapping("/my-books")
    public ApiResponse<List<BookManageDTO>> myManageBooks(HttpSession session) {
        checkMerchant(session);
        Long sellerId = (Long) session.getAttribute("userId");

        // 同时支持URL参数（兼容开发文档）
        // 但优先使用session中的userId，更安全
        List<BookManageDTO> books = bookManageService.getMyManageBooks(sellerId);
        return ApiResponse.success(books);
    }

    /**
     * 可选：兼容开发文档的带参数版本
     */
    @GetMapping("/my-books/param")
    public ApiResponse<List<BookManageDTO>> myBooksByParam(
            @RequestParam(required = false) Long userId,
            HttpSession session) {

        checkMerchant(session);

        Long sellerId;
        if (userId != null) {
            // 如果是管理员查看别人的书籍
            String role = (String) session.getAttribute("userRole");
            if ("ADMIN".equals(role)) {
                sellerId = userId;  // 管理员可以查看任何商家的书籍
            } else {
                // 商家只能查看自己的
                sellerId = (Long) session.getAttribute("userId");
            }
        } else {
            sellerId = (Long) session.getAttribute("userId");
        }

        List<BookManageDTO> books = bookManageService.getMyManageBooks(sellerId);
        return ApiResponse.success(books);
    }

    /**
     * 删除书籍请求DTO
     */
    public static class DeleteBookRequest {
        private Long id;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}