package com.muzify.dto;

import com.muzify.entity.Role;
import com.muzify.entity.User;

public record UserResponse(Long id, String username, String email, Role role) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
    }
}
