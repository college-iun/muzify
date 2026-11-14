package com.muzify.controller;

import com.muzify.dto.AdminStats;
import com.muzify.dto.RoleChangeRequest;
import com.muzify.dto.UserResponse;
import com.muzify.entity.User;
import com.muzify.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) { this.service = service; }

    @GetMapping("/users")
    public Map<String, List<UserResponse>> users() {
        return Map.of("users", service.allUsers());
    }

    @PatchMapping("/users/{id}/role")
    public UserResponse changeRole(@PathVariable Long id, @Valid @RequestBody RoleChangeRequest req,
                                   @AuthenticationPrincipal User current) {
        return service.changeRole(id, req.role(), current);
    }

    @GetMapping("/stats")
    public AdminStats stats() {
        return service.stats();
    }
}
