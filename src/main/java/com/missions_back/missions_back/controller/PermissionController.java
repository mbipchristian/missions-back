package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.PermissionDto;
import com.missions_back.missions_back.dto.PermissionResponseDto;
import com.missions_back.missions_back.model.Permission;
import com.missions_back.missions_back.service.PermissionService;

@RestController
@RequestMapping("/auth/permissions")
public class PermissionController {

    private final PermissionService permissionService;
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    //créer une nouvelle permission
    @PostMapping("/create")
    public ResponseEntity<?> createPermission(@RequestBody PermissionDto permissionDto) {
        try {
            PermissionResponseDto createdPermission = permissionService.createPermission(permissionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de la permission: " + e.getMessage());
        }
    }

    //Récupérer toutes les permissions actives
    @GetMapping("/all")
    public ResponseEntity<List<PermissionResponseDto>> getAllPermissions() {
        List<PermissionResponseDto> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }
}
