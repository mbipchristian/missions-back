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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.VilleDto;
import com.missions_back.missions_back.dto.VilleResponseDto;
import com.missions_back.missions_back.service.VilleService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/auth/villes")
public class VilleController {

    private final VilleService villeService;
    public VilleController(VilleService villeService) {
        this.villeService = villeService;
    }
    @PostMapping("/create")
    public ResponseEntity<?> createVille(@RequestBody VilleDto villeDto) {
        try {
            VilleResponseDto createdVille = villeService.createVille(villeDto);
            return ResponseEntity.ok(createdVille);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de la ville: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllVilles() {
        try {
            List<VilleResponseDto> villes = villeService.getAllVilles();
            return ResponseEntity.ok(villes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération des villes: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getVilleById(@PathVariable Long id) {
        try {
            VilleResponseDto ville = villeService.getVilleById(id);
            return ResponseEntity.ok(ville);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération de la ville: " + e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVille(@PathVariable Long id, @RequestBody VilleDto villeDto) {
        try {
            VilleResponseDto updatedVille = villeService.updateVille(id, villeDto);
            return ResponseEntity.ok(updatedVille);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour de la ville: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVille(@PathVariable Long id) {
        try {
            villeService.deleteVille(id);
            return ResponseEntity.ok("Ville supprimée avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression de la ville: " + e.getMessage());
        }
    }
    @GetMapping("/search/name")
    public ResponseEntity<?> getVillesByName(@RequestParam("q") String name) {
        try {
            List<VilleResponseDto> villes = villeService.getVillesByName(name);
            return ResponseEntity.ok(villes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la recherche par nom: " + e.getMessage());
        }
    }
    @GetMapping("/search/code")
    public ResponseEntity<?> getVillesByCode(@RequestParam("q") String code) {
        try {
            List<VilleResponseDto> villes = villeService.getVillesByCode(code);
            return ResponseEntity.ok(villes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la recherche par code: " + e.getMessage());
        }
    }

}
