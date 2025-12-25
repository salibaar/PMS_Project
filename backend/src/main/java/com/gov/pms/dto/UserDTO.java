package com.gov.pms.dto;

import com.gov.pms.entity.User;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class UserDTO {
    
    private UUID id;
    
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    private String username;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Size(max = 100, message = "Tên đầy đủ tối đa 100 ký tự")
    private String fullName;
    
    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phoneNumber;
    
    @Size(max = 200, message = "Phòng ban tối đa 200 ký tự")
    private String department;
    
    @Size(max = 100, message = "Chức vụ tối đa 100 ký tự")
    private String position;
    
    private Boolean enabled;
    
    private Boolean accountNonLocked;
    
    private Set<User.Role> roles;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastLogin;
}
