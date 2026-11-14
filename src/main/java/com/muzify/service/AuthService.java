package com.muzify.service;

import com.muzify.dto.*;
import com.muzify.entity.Role;
import com.muzify.entity.User;
import com.muzify.exception.ApiException;
import com.muzify.repository.UserRepository;
import com.muzify.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public UserResponse register(RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            throw ApiException.conflict("Пользователь с таким именем уже существует");
        }
        if (users.existsByEmail(req.email())) {
            throw ApiException.conflict("Пользователь с таким email уже существует");
        }
        Role role = req.role() == null ? Role.USER : req.role();
        if (role == Role.ADMIN) {
            throw ApiException.forbidden("Нельзя зарегистрироваться с ролью ADMIN");
        }
        User u = new User();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        u.setRole(role);
        return UserResponse.from(users.save(u));
    }

    public AuthResponse login(LoginRequest req) {
        User u = users.findByUsername(req.username())
                .filter(x -> encoder.matches(req.password(), x.getPassword()))
                .orElseThrow(() -> ApiException.unauthorized("Неверное имя пользователя или пароль"));
        String token = jwt.generateToken(u.getId(), u.getUsername(), u.getRole().name());
        return new AuthResponse(token, "Bearer", u.getRole().name());
    }
}
