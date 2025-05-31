package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    
    
        
    
    
}
