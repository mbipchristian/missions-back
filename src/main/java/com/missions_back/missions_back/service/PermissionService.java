package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.PermissionDto;
import com.missions_back.missions_back.dto.PermissionResponseDto;
import com.missions_back.missions_back.model.Permission;
import com.missions_back.missions_back.repository.PermissionRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PermissionService {
    private final PermissionRepo permissionRepo;

    public PermissionService(PermissionRepo permissionRepo) {
        this.permissionRepo = permissionRepo;
    }

    //Créer une nouvelle permission
    @Transactional
    public PermissionResponseDto createPermission(PermissionDto permissionDto) {
        // Vérifier si le nom de la permission existe déjà
        if (permissionRepo.findByName(permissionDto.name()).isPresent()) {
            throw new IllegalArgumentException("Une permission avec ce nom existe déjà");
        }
        
        Permission permission = new Permission();
        permission.setName(permissionDto.name());
        permission.setIcone(permissionDto.icone());
        permission.setUrl(permissionDto.url());
        permission.setCode(permissionDto.code());
        permission.setActif(true);
        
        Permission createdPermission = permissionRepo.save(permission);
        return convertToPermissionResponseDto(createdPermission);
    }
    //Récupérer toutes les permissions actives
    public List<PermissionResponseDto> getAllPermissions() {
        List<Permission> permissions = permissionRepo.findByActifTrue();
        return permissions.stream()
                     .map(this::convertToPermissionResponseDto)
                     .collect(Collectors.toList());
    }
    // Récupère une permission par son ID
    public PermissionResponseDto getPermissionById(Long id) {
        Permission permission = permissionRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        return convertToPermissionResponseDto(permission);
    }
    
    
     //Met à jour une permission existante
    @Transactional
    public PermissionResponseDto updatePermission(Long id, PermissionDto permissionDto) {
        Permission permission = permissionRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        
        // Vérifier si le nouveau nom existe déjà pour une autre permission
        Optional<Permission> existingPermissionWithName = permissionRepo.findByName(permissionDto.name());
        if (existingPermissionWithName.isPresent() && !existingPermissionWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Un autre rôle avec ce nom existe déjà");
        }
        
        permission.setName(permissionDto.name());
        
        Permission updatedPermission = permissionRepo.save(permission);
        return convertToPermissionResponseDto(updatedPermission);
    }
    
    // Supprime logiquement une permission
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        permission.setActif(false);
        permission.setDeleted_at(LocalDateTime.now());
        permissionRepo.save(permission);
    }

    // Méthode de conversion de Permission vers PermissionResponseDto
    private PermissionResponseDto convertToPermissionResponseDto(Permission permission) {
    return new PermissionResponseDto(
        permission.getId(),
        permission.getName(),
        permission.getCode(),
        permission.getCreated_at(),
        permission.getUpdated_at()    );
}
}
