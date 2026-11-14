package com.muzify.dto;

import com.muzify.entity.Role;
import jakarta.validation.constraints.NotNull;

public record RoleChangeRequest(@NotNull(message = "Не указана роль") Role role) { }
