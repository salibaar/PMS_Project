package com.gov.pms.service;

import com.gov.pms.dto.RegisterRequest;
import com.gov.pms.dto.UserDTO;
import com.gov.pms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    
    User createUser(RegisterRequest registerRequest);
    
    User updateUser(UUID id, UserDTO userDTO);
    
    void deleteUser(UUID id);
    
    User getUserById(UUID id);
    
    User getUserByUsername(String username);
    
    User getUserByEmail(String email);
    
    Page<User> getAllUsers(Pageable pageable);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    UserDTO convertToDTO(User user);
    
    User convertToEntity(UserDTO userDTO);
}
