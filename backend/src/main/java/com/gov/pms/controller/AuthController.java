package com.gov.pms.controller;

import com.gov.pms.dto.JwtResponse;
import com.gov.pms.dto.LoginRequest;
import com.gov.pms.dto.RegisterRequest;
import com.gov.pms.dto.UserDTO;
import com.gov.pms.entity.User;
import com.gov.pms.security.CustomUserDetailsService;
import com.gov.pms.security.JwtTokenProvider;
import com.gov.pms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Authentication", description = "API xác thực và quản lý người dùng")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Operation(summary = "Đăng nhập", description = "Đăng nhập với username/email và password để nhận JWT token")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        
        // Load user entity and update last login
        User user = customUserDetailsService.loadUserEntityByUsername(loginRequest.getUsernameOrEmail());
        user.setLastLogin(LocalDateTime.now());
        
        UserDTO userDTO = userService.convertToDTO(user);
        
        return ResponseEntity.ok(new JwtResponse(jwt, userDTO));
    }
    
    @Operation(summary = "Đăng ký", description = "Đăng ký tài khoản người dùng mới")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        
        if (userService.existsByUsername(registerRequest.getUsername())) {
            Map<String, String> error = new HashMap<>();
            error.put("username", "Username đã tồn tại");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        if (userService.existsByEmail(registerRequest.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("email", "Email đã tồn tại");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        User user = userService.createUser(registerRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đăng ký thành công");
        response.put("user", userService.convertToDTO(user));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Lấy thông tin user hiện tại", description = "Lấy thông tin của user đang đăng nhập")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = customUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userService.convertToDTO(user));
    }
}
