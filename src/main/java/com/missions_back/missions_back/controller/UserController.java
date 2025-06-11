package com.missions_back.missions_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.LoginResponse;
import com.missions_back.missions_back.dto.LoginUserDto;
import com.missions_back.missions_back.dto.RegisterUserDto;
import com.missions_back.missions_back.dto.UserResponseDto;
import com.missions_back.missions_back.dto.UserRoleUpdateDto;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.service.UserService;

import jakarta.persistence.EntityNotFoundException;

import com.missions_back.missions_back.service.JwtService;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final JwtService jwtService;
    private final UserService userService; 
    
    public UserController(JwtService jwtService, UserService authenticationService) {
        this.jwtService = jwtService;
        this.userService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = userService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        var authenticatedUser = userService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
    // Nouveau endpoint pour récupérer les informations de l'utilisateur connecté
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            UserResponseDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // @PostMapping("{id}/restaurer")
    // public ResponseEntity<Void> restaure (@PathVariable Long id){
        
    // }
    //Récuperer tous les users
    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        }
    }
    //Récupérer un utilisateur par son id
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        try {
            UserResponseDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    //Récupérer un utilisateur par son email
    @GetMapping("/users/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        try {
            UserResponseDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    //Récupérer un utilisateur par son username
    @GetMapping("/users/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        try {
            UserResponseDto user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Changer le role d'un utilisateur
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(
            @PathVariable Long id,
            @RequestBody UserRoleUpdateDto updateDto) {
        
        if (!id.equals(updateDto.userId())) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            UserResponseDto updatedUser = userService.updateUserRole(updateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
