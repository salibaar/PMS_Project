package com.gov.pms.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Username hoặc email không được để trống")
    private String usernameOrEmail;
    
    @NotBlank(message = "Password không được để trống")
    private String password;
}
