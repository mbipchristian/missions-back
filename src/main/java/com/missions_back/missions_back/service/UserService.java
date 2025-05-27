package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.GradeResponseDto;
import com.missions_back.missions_back.dto.LoginUserDto;
import com.missions_back.missions_back.dto.PermissionResponseDto;
import com.missions_back.missions_back.dto.RegisterUserDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.dto.UserResponseDto;
import com.missions_back.missions_back.dto.UserRoleUpdateDto;
import com.missions_back.missions_back.model.Grade;
import com.missions_back.missions_back.model.Role;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.GradeRepo;
import com.missions_back.missions_back.repository.RoleRepo;
import com.missions_back.missions_back.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private RoleRepo roleRepo;
    private GradeRepo gradeRepo;
    

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RoleRepo roleRepo, GradeRepo gradeRepo) {
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
                .setQuotaAnnuel(input.quotaAnnuel())
                .setPassword(passwordEncoder.encode(input.password()))
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setActif(true);

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
    //Recupérer tous les users (actifs forcement !)
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepo.findByActifTrue();
        return users.stream()
            .map(this::convertToUserResponseDto)
            .collect(Collectors.toList());
    }
    // Récupérer un utilisateur par son ID
    public UserResponseDto getUserById(Long id) {
        User user = userRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        return convertToUserResponseDto(user);
    }
    // Récupérer un utilisateur par son email
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'email : " + email));
        return convertToUserResponseDto(user);
    }
    //Récupérer un utilisateur par son username
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec le nom : " + username));
        return convertToUserResponseDto(user);
    }
    // Récupérer un utilisateur par son matricule
    public UserResponseDto getUserByMatricule(String matricule) {
        User user = userRepo.findByMatricule(matricule)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec le matricule : " + matricule));
        return convertToUserResponseDto(user);
    }
    // Méthode pour mettre à jour le rôle d'un utilisateur
    @Transactional
    public UserResponseDto updateUserRole(UserRoleUpdateDto updateDto) {
        User user = userRepo.findById(updateDto.userId())
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + updateDto.userId()));
        
        Role role = roleRepo.findById(updateDto.roleId())
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID: " + updateDto.roleId()));
        
        user.setRole(role);
        User updatedUser = userRepo.save(user);
        return convertToUserResponseDto(updatedUser);
    }

    // Méthode pour convertir un utilisateur en UserResponseDto
    private UserResponseDto convertToUserResponseDto(User user) {
        RoleResponseDto roleDto = null;
        GradeResponseDto gradeDto = null;
        if (user.getRole() != null && user.getRole() != null) {
            roleDto = convertToRoleResponseDto(user.getRole());
            gradeDto = convertToGradeResponseDto(user.getGrade());
        }
        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getMatricule(),
            user.getQuotaAnnuel(),
            roleDto,  // Un seul rôle au lieu d'une liste
            gradeDto,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );

    }

    private RoleResponseDto convertToRoleResponseDto(Role role) {
        List<PermissionResponseDto> permissionDtos = role.getPermissions() != null 
            ? role.getPermissions().stream()
                .map(permission -> new PermissionResponseDto(
                    permission.getId(),
                    permission.getName(),
                    permission.getCode(),
                    permission.getCreated_at(),
                    permission.getUpdated_at()
                ))
                .collect(Collectors.toList())
            : List.of();
        
        return new RoleResponseDto(
            role.getId(),
            role.getCode(),
            role.getName(),
            role.getCreated_at(),
            role.getUpdated_at(),
            permissionDtos
        );
    }

    private GradeResponseDto convertToGradeResponseDto(Grade grade) {
        return new GradeResponseDto(
            grade.getId(),
            grade.getName(),
            grade.getCode(),
            grade.getFraisExterne(),
            grade.getFraisInterne(),
            grade.getCreated_at(),
            grade.getUpdated_at()
        );
    }
    
}
