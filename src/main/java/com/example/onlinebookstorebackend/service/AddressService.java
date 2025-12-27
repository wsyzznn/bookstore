package com.example.onlinebookstorebackend.service;

import com.example.onlinebookstorebackend.common.BusinessException;
import com.example.onlinebookstorebackend.dto.AddressDTO;
import com.example.onlinebookstorebackend.dto.AddressResponseDTO;
import com.example.onlinebookstorebackend.entity.Address;
import com.example.onlinebookstorebackend.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * 获取用户地址列表 - 返回AddressResponseDTO（不包含userId）
     */
    public List<AddressResponseDTO> getUserAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);

        return addresses.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 添加地址 - 不返回数据，只抛出异常或成功
     */
    @Transactional
    public void addAddress(AddressDTO dto) {
        // 验证必要字段
        validateAddressDTO(dto);

        // 转换为实体
        Address address = convertToEntity(dto);
        address.setCreateTime(LocalDateTime.now());

        // 保存到数据库（不需要返回）
        addressRepository.save(address);
    }

    /**
     * 更新地址 - 不返回数据，只抛出异常或成功
     */
    @Transactional
    public void updateAddress(AddressDTO dto) {
        // 验证必要字段
        validateAddressDTO(dto);

        // 检查地址是否存在
        Address existing = addressRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("地址不存在，ID: " + dto.getId()));

        // 检查权限
        if (!existing.getUserId().equals(dto.getUserId())) {
            throw new BusinessException(403, "无权修改该地址");
        }

        // 更新字段
        existing.setRecipientName(dto.getRecipientName());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setAddressDetail(dto.getAddressDetail());

        // 保存更新（不需要返回）
        addressRepository.save(existing);
    }

    /**
     * 删除地址
     */
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        if (addressId == null) {
            throw new BusinessException("地址ID不能为空");
        }

        // 查找地址
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException("地址不存在，ID: " + addressId));

        // 检查权限
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除该地址");
        }

        // 执行删除
        addressRepository.delete(address);
    }

    /**
     * 验证地址DTO
     */
    private void validateAddressDTO(AddressDTO dto) {
        if (dto.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (dto.getRecipientName() == null || dto.getRecipientName().trim().isEmpty()) {
            throw new BusinessException("收件人姓名不能为空");
        }
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().trim().isEmpty()) {
            throw new BusinessException("联系电话不能为空");
        }
        if (dto.getAddressDetail() == null || dto.getAddressDetail().trim().isEmpty()) {
            throw new BusinessException("详细地址不能为空");
        }

        // 验证手机号格式
        if (!dto.getPhoneNumber().matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }
    }

    /**
     * 实体转响应DTO（不包含userId）
     */
    private AddressResponseDTO convertToResponseDTO(Address address) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(address.getId());
        dto.setRecipientName(address.getRecipientName());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setAddressDetail(address.getAddressDetail());
        return dto;
    }

    /**
     * DTO转实体
     */
    private Address convertToEntity(AddressDTO dto) {
        Address address = new Address();
        address.setId(dto.getId());
        address.setUserId(dto.getUserId());
        address.setRecipientName(dto.getRecipientName());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setAddressDetail(dto.getAddressDetail());
        return address;
    }
}