package com.gov.pms.service;

import com.gov.pms.dto.RegisterRequest;
import com.gov.pms.dto.UserDTO;
import com.gov.pms.entity.User;
import com.gov.pms.exception.ResourceNotFoundException;
import com.gov.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User createUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setDepartment(registerRequest.getDepartment());
        user.setPosition(registerRequest.getPosition());
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Set roles - default to ROLE_USER if not specified
        Set<User.Role> roles = new HashSet<>();
        if (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()) {
            roles.addAll(registerRequest.getRoles());
        } else {
            roles.add(User.Role.ROLE_USER);
        }
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(UUID id, UserDTO userDTO) {
        User user = getUserById(id);
        
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFullName() != null) {
            user.setFullName(userDTO.getFullName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getDepartment() != null) {
            user.setDepartment(userDTO.getDepartment());
        }
        if (userDTO.getPosition() != null) {
            user.setPosition(userDTO.getPosition());
        }
        if (userDTO.getEnabled() != null) {
            user.setEnabled(userDTO.getEnabled());
        }
        if (userDTO.getAccountNonLocked() != null) {
            user.setAccountNonLocked(userDTO.getAccountNonLocked());
        }
        if (userDTO.getRoles() != null) {
            user.setRoles(userDTO.getRoles());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user); // Soft delete
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDepartment(user.getDepartment());
        dto.setPosition(user.getPosition());
        dto.setEnabled(user.getEnabled());
        dto.setAccountNonLocked(user.getAccountNonLocked());
        dto.setRoles(user.getRoles());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
    
    @Override
    public User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setDepartment(userDTO.getDepartment());
        user.setPosition(userDTO.getPosition());
        user.setEnabled(userDTO.getEnabled() != null ? userDTO.getEnabled() : true);
        user.setAccountNonLocked(userDTO.getAccountNonLocked() != null ? userDTO.getAccountNonLocked() : true);
        user.setRoles(userDTO.getRoles() != null ? userDTO.getRoles() : new HashSet<>());
        return user;
    }
}
