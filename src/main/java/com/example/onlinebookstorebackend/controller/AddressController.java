package com.example.onlinebookstorebackend.controller;

import com.example.onlinebookstorebackend.common.BusinessException;
import com.example.onlinebookstorebackend.dto.*;
import com.example.onlinebookstorebackend.service.AddressService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取我的地址列表 - GET /api/addresses
     * 开发文档示例：
     * {
     *   "code": 200,
     *   "msg": "success",
     *   "data": [
     *     {
     *       "id": 1,
     *       "recipientName": "张三",
     *       "phoneNumber": "13800001111",
     *       "addressDetail": "北京市..."
     *     }
     *   ]
     * }
     */
    @GetMapping
    public ApiResponse<List<AddressResponseDTO>> list(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        List<AddressResponseDTO> addresses = addressService.getUserAddresses(userId);
        return ApiResponse.success(addresses);
    }

    /**
     * 新增地址 - POST /api/addresses/add
     * 开发文档示例：
     * 请求：{
     *   "userId": 1001,  // ❌ 实际上不应该由前端传递
     *   "recipientName": "李四",
     *   "phoneNumber": "13900002222",
     *   "addressDetail": "南京市鼓楼区..."
     * }
     * 响应：{ "code": 200, "msg": "地址添加成功", "data": null }
     */
    @PostMapping("/add")
    public ApiResponse<Void> add(
            @Valid @RequestBody AddressAddDTO addressDTO,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 将AddressAddDTO转换为内部的AddressDTO
        AddressDTO internalDTO = new AddressDTO();
        internalDTO.setRecipientName(addressDTO.getRecipientName());
        internalDTO.setPhoneNumber(addressDTO.getPhoneNumber());
        internalDTO.setAddressDetail(addressDTO.getAddressDetail());
        internalDTO.setUserId(userId);

        addressService.addAddress(internalDTO);
        return ApiResponse.success("地址添加成功", null);  // data为null
    }

    /**
     * 修改地址 - POST /api/addresses/update
     * 开发文档示例：
     * {
     *   "id": 1,
     *   "recipientName": "张三",
     *   "phoneNumber": "13888888888",
     *   "addressDetail": "新地址..."
     * }
     * 响应：{ "code": 200, "msg": "地址修改成功", "data": null }
     */
    @PostMapping("/update")
    public ApiResponse<Void> update(
            @Valid @RequestBody AddressDTO addressDTO,  // 这里可以复用AddressDTO，因为需要id字段
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        addressDTO.setUserId(userId);
        addressService.updateAddress(addressDTO);
        return ApiResponse.success("地址修改成功", null);  // data为null
    }

    /**
     * 删除地址 - POST /api/addresses/delete
     * 开发文档示例：
     * 请求：{ "id": 1 }
     * 响应：{ "code": 200, "msg": "地址已删除", "data": null }
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(
            @RequestBody Map<String, Long> request,  // 简化，直接使用Map
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Long addressId = request.get("id");
        if (addressId == null) {
            throw new BusinessException("地址ID不能为空");
        }

        addressService.deleteAddress(addressId, userId);
        return ApiResponse.success("地址已删除", null);  // data为null
    }
}