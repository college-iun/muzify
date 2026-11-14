package com.muzify.dto;

import com.muzify.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Не указано имя пользователя")
        @Size(min = 3, max = 50, message = "Имя пользователя: от 3 до 50 символов")
        String username,

        @NotBlank(message = "Не указан email")
        @Email(message = "Некорректный email")
        String email,

        @NotBlank(message = "Не указан пароль")
        @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
        String password,

        Role role // USER (по умолчанию) или ARTIST; ADMIN самостоятельно получить нельзя
) { }
