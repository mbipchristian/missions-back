package com.missions_back.missions_back.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.missions_back.missions_back.dto.LoginUserDto;
import com.missions_back.missions_back.dto.RegisterUserDto;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.UserRepo;

@Service
public class AuthService {
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User signup(RegisterUserDto input) {
        User user = new User()
                .setName(input.username())
                .setEmail(input.email())
                .setPassword(passwordEncoder.encode(input.password()));

        return userRepo.save(user);
    }

    public UserDetails authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(input.email(), input.password())
        );
        return userRepo.findByEmail(input.email())
        .orElseThrow();
    }
}
