package com.missions_back.missions_back.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.model.Role;
import com.missions_back.missions_back.model.RoleEnum;
import com.missions_back.missions_back.repository.RoleRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RoleService {

    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Optional<Role> findByName(RoleEnum name) {
        return roleRepo.findByName(name);
    }
    
    //Récupérer tous les rôles
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = roleRepo.findAll();
        return roles.stream()
            .map(this::convertToRoleResponseDto)
            .collect(Collectors.toList());
    }
    
    
     // Récupère un rôle par son ID
    public RoleResponseDto getRoleById(Long id) {
        Role role = roleRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        return convertToRoleResponseDto(role);
    }

    /**
     * Convertit une entité Role en DTO de réponse
     */
    private RoleResponseDto convertToRoleResponseDto(Role role) {
        
        
        return new RoleResponseDto(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getCreated_at(),
            role.getUpdated_at()
            
        );
    }
}
