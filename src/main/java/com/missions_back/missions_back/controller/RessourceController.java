package com.missions_back.missions_back.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.RessourceDto;
import com.missions_back.missions_back.dto.RessourceResponseDto;
import com.missions_back.missions_back.service.RessourceService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/auth/ressources")
public class RessourceController {

    private final RessourceService ressourceService;

    public RessourceController(RessourceService ressourceService) {
        this.ressourceService = ressourceService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<RessourceResponseDto>> getAllRessources() {
        List<RessourceResponseDto> ressources = ressourceService.getAllRessources();
        return ResponseEntity.ok(ressources);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RessourceResponseDto> getRessourceById(@PathVariable Long id) {
        return ressourceService.getRessourceById(id)
                .map(ressource -> ResponseEntity.ok(ressource))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<RessourceResponseDto> getRessourceByCode(@PathVariable String code) {
        return ressourceService.getRessourceByCode(code)
                .map(ressource -> ResponseEntity.ok(ressource))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<RessourceResponseDto>> searchRessources(
            @RequestParam String name) {
        List<RessourceResponseDto> ressources = ressourceService.searchRessourcesByName(name);
        return ResponseEntity.ok(ressources);
    }
    
    @PostMapping("/create")
    public ResponseEntity<RessourceResponseDto> createRessource(@RequestBody RessourceDto dto) {
        try {
            RessourceResponseDto createdRessource = ressourceService.createRessource(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRessource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RessourceResponseDto> updateRessource(
            @PathVariable Long id, 
            @RequestBody RessourceDto dto) {
        try {
            return ressourceService.updateRessource(id, dto)
                    .map(ressource -> ResponseEntity.ok(ressource))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRessource(@PathVariable Long id) {
        try {
            ressourceService.deleteRessource(id);
            return ResponseEntity.ok("Ressource supprimée avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression de la ressource: " + e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/quantite")
    public ResponseEntity<RessourceResponseDto> updateQuantite(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        Long nouvelleQuantite = request.get("quantite");
        if (nouvelleQuantite == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ressourceService.updateQuantite(id, nouvelleQuantite)
                .map(ressource -> ResponseEntity.ok(ressource))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/ajouter")
    public ResponseEntity<RessourceResponseDto> ajouterQuantite(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        Long quantiteAAjouter = request.get("quantite");
        if (quantiteAAjouter == null || quantiteAAjouter <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        return ressourceService.ajouterQuantite(id, quantiteAAjouter)
                .map(ressource -> ResponseEntity.ok(ressource))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/retirer")
    public ResponseEntity<RessourceResponseDto> retirerQuantite(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        Long quantiteARetirer = request.get("quantite");
        if (quantiteARetirer == null || quantiteARetirer <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            return ressourceService.retirerQuantite(id, quantiteARetirer)
                    .map(ressource -> ResponseEntity.ok(ressource))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
    }
}
