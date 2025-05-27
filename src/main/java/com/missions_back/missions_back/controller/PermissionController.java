package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.PermissionDto;
import com.missions_back.missions_back.dto.PermissionResponseDto;
import com.missions_back.missions_back.service.PermissionService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("auth/permissions")
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
    //Récupérer une permission par son id
    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> getPermissionById(@PathVariable Long id) {
        try {
            PermissionResponseDto permission = permissionService.getPermissionById(id);
            return new ResponseEntity<>(permission, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //Mettre à jour une permission existante
    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> updatePermission(
            @PathVariable Long id, 
            @RequestBody PermissionDto permissionDto) {
        try {
            PermissionResponseDto updatedPermission = permissionService.updatePermission(id, permissionDto);
            return new ResponseEntity<>(updatedPermission, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    //Supprimer une permission
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
