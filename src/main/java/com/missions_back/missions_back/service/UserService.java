package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.LoginUserDto;
import com.missions_back.missions_back.dto.RangResponseDto;
import com.missions_back.missions_back.dto.RegisterUserDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.dto.UserResponseDto;
import com.missions_back.missions_back.dto.UserRoleUpdateDto;
import com.missions_back.missions_back.model.Rang;
import com.missions_back.missions_back.model.Role;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.RangRepository;
import com.missions_back.missions_back.repository.RoleRepo;
import com.missions_back.missions_back.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private RoleRepo roleRepo;
    private RangRepository rangRepo;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RoleRepo roleRepo, RangRepository rangRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepo = roleRepo;
        this.rangRepo = rangRepo;
    }

    public User signup(RegisterUserDto input) {

        // Récupérer le role
        Role role = roleRepo.findById(input.roleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID : " + input.roleId()));

        // Récupérer le rang (si fourni)
        Rang rang = null;
        if (input.rangId() != null) {
            rang = rangRepo.findById(input.rangId())
                    .orElseThrow(() -> new EntityNotFoundException("Rang not found with ID : " + input.rangId()));
        }

        // Créer et configurer l'utilisateur
        User user = new User()
                .setName(input.username())
                .setEmail(input.email())
                .setMatricule(input.matricule())
                .setQuotaAnnuel(input.quotaAnnuel())
                .setFonction(input.fonction())
                .setPassword(passwordEncoder.encode(input.password()))
                .setCreatedAt(LocalDateTime.now()) // Correction: setCreated_at au lieu de setCreatedAt
                .setUpdatedAt(LocalDateTime.now()) // Correction: setUpdated_at au lieu de setUpdatedAt
                .setActif(true);

        // Associer le rôle et le rang à l'utilisateur
        user.setRole(role);
        if (rang != null) {
            user.setRang(rang);
        }
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
        deletedUser.setDeleted_at(LocalDateTime.now()); // Correction: setDeleted_at au lieu de setDeletedAt

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
        user.setUpdated_at(LocalDateTime.now()); // Mise à jour du timestamp
        User updatedUser = userRepo.save(user);
        return convertToUserResponseDto(updatedUser);
    }

    // Méthode pour convertir un utilisateur en UserResponseDto
    private UserResponseDto convertToUserResponseDto(User user) {
        RoleResponseDto roleDto = null;
        if (user.getRole() != null) { // Correction: suppression de la condition redondante
            roleDto = convertToRoleResponseDto(user.getRole());
        }

        RangResponseDto rangDto = null;
        if (user.getRang() != null) { // Correction: suppression de la condition redondante
            rangDto = convertToRangResponseDto(user.getRang());
        }
        
        return new UserResponseDto(
            user.getId(),
            user.getName(), 
            user.getEmail(),
            user.getMatricule(),
            user.getQuotaAnnuel(),
            roleDto,  
            rangDto,
            user.getFonction(),
            user.getCreated_at(), // Correction: getCreated_at au lieu de getCreatedAt
            user.getUpdated_at()  // Correction: getUpdated_at au lieu de getUpdatedAt
        );
    }

    private RoleResponseDto convertToRoleResponseDto(Role role) {
        return new RoleResponseDto(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getCreated_at(),
            role.getUpdated_at()
        );
    }
    private RangResponseDto convertToRangResponseDto(Rang rang) {
        return new RangResponseDto(
            rang.getId(),
            rang.getNom(),
            rang.getCode(),
            rang.getFraisExterne(),
            rang.getFraisInterne(),
            rang.getCreated_at(),
            rang.getUpdated_at()        );
    }
}