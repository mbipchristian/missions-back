package com.missions_back.missions_back.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.PermissionDto;
import com.missions_back.missions_back.dto.PermissionResponseDto;
import com.missions_back.missions_back.model.Permission;
import com.missions_back.missions_back.repository.PermissionRepo;

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
        permission.setDescription(permissionDto.description());
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

    // Méthode de conversion de Permission vers PermissionResponseDto
    private PermissionResponseDto convertToPermissionResponseDto(Permission permission) {
    return new PermissionResponseDto(
        permission.getId(),
        permission.getName(),
        permission.getCode(),
        permission.getCreated_at(),
        permission.getUpdated_at(),
        permission.isActif()
    );
}
}
