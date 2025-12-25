package com.gov.pms.dto;

import com.gov.pms.entity.User;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Set;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    private String username;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100, message = "Password phải từ 6 đến 100 ký tự")
    private String password;
    
    @Size(max = 100, message = "Tên đầy đủ tối đa 100 ký tự")
    private String fullName;
    
    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phoneNumber;
    
    @Size(max = 200, message = "Phòng ban tối đa 200 ký tự")
    private String department;
    
    @Size(max = 100, message = "Chức vụ tối đa 100 ký tự")
    private String position;
    
    private Set<User.Role> roles;
}
