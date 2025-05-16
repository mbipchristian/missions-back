package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.RoleDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.model.Role;
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

    
}
