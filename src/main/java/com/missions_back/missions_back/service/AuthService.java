package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.LoginUserDto;
import com.missions_back.missions_back.dto.RegisterUserDto;
import com.missions_back.missions_back.model.Grade;
import com.missions_back.missions_back.model.Role;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.GradeRepo;
import com.missions_back.missions_back.repository.RoleRepo;
import com.missions_back.missions_back.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService {
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private RoleRepo roleRepo;
    private GradeRepo gradeRepo;
    

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RoleRepo roleRepo, GradeRepo gradeRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepo = roleRepo;
        this.gradeRepo = gradeRepo;
    }

    public User signup(RegisterUserDto input) {

        // Récupérer le role et le grade
        Role role = roleRepo.findById(input.roleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID : " + input.roleId()));

        Grade grade = gradeRepo.findById(input.gradeId())
                .orElseThrow(() -> new EntityNotFoundException("Grade not found with ID : " + input.gradeId()));

        // Créer et configurer l'utilisateur
        User user = new User()
                .setName(input.username())
                .setEmail(input.email())
                .setMatricule(input.matricule())
                .setPassword(passwordEncoder.encode(input.password()));

        // Associer le rôle et le grade à l'utilisateur
        user.setRole(role);
        user.setGrade(grade);
        return userRepo.save(user);
    }

    public UserDetails authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(input.email(), input.password())
        );
        return userRepo.findByEmail(input.email())
        .orElseThrow();
    }

    @Transactional
    public void delete(Long id) {
        User deletedUser = userRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Entité non trouvée avec l'ID : " + id));

        deletedUser.setActif(false);
        deletedUser.setDeletedAt(LocalDateTime.now());

        userRepo.save(deletedUser);
    }
}
