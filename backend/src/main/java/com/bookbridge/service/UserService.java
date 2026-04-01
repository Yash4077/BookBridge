package com.bookbridge.service;

import com.bookbridge.config.JwtUtil;
import com.bookbridge.dto.*;
import com.bookbridge.model.User;
import com.bookbridge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public UserService(UserRepository repo, PasswordEncoder encoder, JwtUtil jwt) {
        this.repo = repo; this.encoder = encoder; this.jwt = jwt;
    }

    public AuthResponse signup(AuthRequest req) {
        if (repo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered");
        User u = new User();
        u.setName(req.getName()); u.setEmail(req.getEmail());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setDepartment(req.getDepartment()); u.setPhone(req.getPhone());
        repo.save(u);
        return new AuthResponse(jwt.generate(u.getEmail()), u.getId(), u.getName(), u.getEmail(), u.getDepartment());
    }

    public AuthResponse login(AuthRequest req) {
        User u = repo.findByEmail(req.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!encoder.matches(req.getPassword(), u.getPassword()))
            throw new RuntimeException("Invalid password");
        return new AuthResponse(jwt.generate(u.getEmail()), u.getId(), u.getName(), u.getEmail(), u.getDepartment());
    }

    public User getByEmail(String email) {
        return repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
