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

import com.missions_back.missions_back.dto.RoleDto;
import com.missions_back.missions_back.dto.RolePermissionUpdateDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.service.RoleService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/auth/roles")
public class RoleController {
    private RoleService roleService;

    public RoleController (RoleService roleService) {
        this.roleService = roleService;
    }

    //Créer un nouveau role
    @PostMapping("/create")
    public ResponseEntity<?> createRole(@RequestBody RoleDto roleDto) {
        try {
            RoleResponseDto createdRole = roleService.createRole(roleDto);
            return ResponseEntity.ok(createdRole);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création du rôle: " + e.getMessage());
        }
    }

    //Récupérer tous les roles
    @GetMapping("/all")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
    //Récupérer un role par son id
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        try {
            RoleResponseDto role = roleService.getRoleById(id);
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    //Mettre à jour un role
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleDto roleDto) {
        try {
            RoleResponseDto updatedRole = roleService.updateRole(id, roleDto);
            return ResponseEntity.ok(updatedRole);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour du rôle: " + e.getMessage());
        }

        
    }
    //Supprimer un role
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //Mettre à jour les permissions d'un role
    @PutMapping("/permissions")
    public ResponseEntity<RoleResponseDto> updateRolePermissions(@RequestBody RolePermissionUpdateDto updateDto) {
        try {
            RoleResponseDto updatedRole = roleService.updateRolePermissions(updateDto);
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //Ajouter une permission à un role
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<RoleResponseDto> addPermissionToRole(
            @PathVariable Long roleId, 
            @PathVariable Long permissionId) {
        try {
            RoleResponseDto updatedRole = roleService.addPermissionToRole(roleId, permissionId);
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //Supprimer une permission d'un role
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<RoleResponseDto> removePermissionFromRole(
            @PathVariable Long roleId, 
            @PathVariable Long permissionId) {
        try {
            RoleResponseDto updatedRole = roleService.removePermissionFromRole(roleId, permissionId);
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    
}
