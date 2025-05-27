package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.PermissionResponseDto;
import com.missions_back.missions_back.dto.RoleDto;
import com.missions_back.missions_back.dto.RolePermissionUpdateDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.model.Permission;
import com.missions_back.missions_back.model.Role;
import com.missions_back.missions_back.repository.PermissionRepo;
import com.missions_back.missions_back.repository.RoleRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RoleService {
    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;

    public RoleService(RoleRepo roleRepo, PermissionRepo permissionRepo) {
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
    }

    //Creation d'un nouveau role

    @Transactional
    public RoleResponseDto createRole(RoleDto roleDto) {
        // Vérifier si le rôle existe déjà
        if (roleRepo.findByName(roleDto.name()).isPresent()) {
            throw new IllegalArgumentException("Role already exists");
        }

        // Créer un nouveau rôle
        Role role = new Role();
        role.setName(roleDto.name());
        role.setCode(roleDto.code());
        role.setCreated_at(LocalDateTime.now());
        role.setUpdated_at(LocalDateTime.now());
        role.setActif(true);

        // Ajouter les permissions au rôle
        if (roleDto.permissionIds() != null && !roleDto.permissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepo.findAllById(roleDto.permissionIds());
            
            // Vérifier si toutes les permissions ont été trouvées
            if (permissions.size() != roleDto.permissionIds().size()) {
                throw new EntityNotFoundException("Une ou plusieurs permissions n'ont pas été trouvées");
            }
            
            role.setPermissions(permissions);
        }

        Role createdRole = roleRepo.save(role);
        return convertToRoleResponseDto(createdRole);
    }
    //Récupérer tous les rôles actifs
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = roleRepo.findByActifTrue();
        return roles.stream()
            .map(this::convertToRoleResponseDto)
            .collect(Collectors.toList());
    }
    
    
     // Récupère un rôle par son ID
    public RoleResponseDto getRoleById(Long id) {
        Role role = roleRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        return convertToRoleResponseDto(role);
    }
    
    
     //Met à jour un rôle existant
    @Transactional
    public RoleResponseDto updateRole(Long id, RoleDto roleDto) {
        Role role = roleRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        
        // Vérifier si le nouveau nom existe déjà pour un autre rôle
        Optional<Role> existingRoleWithName = roleRepo.findByName(roleDto.name());
        if (existingRoleWithName.isPresent() && !existingRoleWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Un autre rôle avec ce nom existe déjà");
        }
        
        role.setName(roleDto.name());
        
        // Mettre à jour les permissions si fournies
        if (roleDto.permissionIds() != null) {
            List<Permission> permissions = permissionRepo.findAllById(roleDto.permissionIds());
            
            // Vérifier si toutes les permissions ont été trouvées
            if (!roleDto.permissionIds().isEmpty() && permissions.size() != roleDto.permissionIds().size()) {
                throw new EntityNotFoundException("Une ou plusieurs permissions n'ont pas été trouvées");
            }
            
            role.setPermissions(permissions);
        }
        
        Role updatedRole = roleRepo.save(role);
        return convertToRoleResponseDto(updatedRole);
    }
    
    // Supprime logiquement un role
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        role.setActif(false);
        role.setDeleted_at(LocalDateTime.now());
        roleRepo.save(role);
    }

    // Méthode pour mettre à jour les permissions d'un rôle
    @Transactional
    public RoleResponseDto updateRolePermissions(RolePermissionUpdateDto updateDto) {
        Role role = roleRepo.findById(updateDto.roleId())
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID: " + updateDto.roleId()));
        
        // Récupérer toutes les permissions demandées
        List<Permission> permissions = updateDto.permissionIds().stream()
            .map(id -> permissionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Permission non trouvée avec l'ID: " + id)))
            .collect(Collectors.toList());
        
        // Mettre à jour les permissions du rôle
        role.setPermissions(permissions);
        role.setUpdated_at(LocalDateTime.now());
        
        Role updatedRole = roleRepo.save(role);
        return convertToRoleResponseDto(updatedRole);
    }

    // Méthode pour ajouter une permission à un rôle
    @Transactional
    public RoleResponseDto addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepo.findById(roleId)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID: " + roleId));
            
        Permission permission = permissionRepo.findById(permissionId)
            .orElseThrow(() -> new EntityNotFoundException("Permission non trouvée avec l'ID: " + permissionId));
            
        role.getPermissions().add(permission);
        role.setUpdated_at(LocalDateTime.now());
        
        Role updatedRole = roleRepo.save(role);
        return convertToRoleResponseDto(updatedRole);
    }

    // Méthode pour supprimer une permission d'un rôle
    @Transactional
    public RoleResponseDto removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepo.findById(roleId)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID: " + roleId));
            
        role.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        role.setUpdated_at(LocalDateTime.now());
        
        Role updatedRole = roleRepo.save(role);
        return convertToRoleResponseDto(updatedRole);
    }

    /**
     * Convertit une entité Role en DTO de réponse
     */
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
}
