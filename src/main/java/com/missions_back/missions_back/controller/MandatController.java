package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.MandatDto;
import com.missions_back.missions_back.dto.MandatResponseDto;
import com.missions_back.missions_back.service.MandatService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/auth/mandats")
public class MandatController {
    private final MandatService mandatService;
    public MandatController(MandatService mandatService) {
        this.mandatService = mandatService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMandat( @RequestBody MandatDto mandatDto) {
        try {
            MandatResponseDto createdMandat = mandatService.createMandat(mandatDto);
            return ResponseEntity.ok(createdMandat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création du mandat: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllMandats() {
        try {
            List<MandatResponseDto> mandats = mandatService.getAllMandats();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération des mandats: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMandatById(@PathVariable Long id) {
        try {
            MandatResponseDto mandat = mandatService.getMandatById(id);
            return ResponseEntity.ok(mandat);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération du mandat: " + e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMandat(@PathVariable Long id, @RequestBody MandatDto mandatDto) {
        try {
            MandatResponseDto updatedMandat = mandatService.updateMandat(id, mandatDto);
            return ResponseEntity.ok(updatedMandat);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour du mandat: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMandat(@PathVariable Long id) {
        try {
            mandatService.deleteMandat(id);
            return ResponseEntity.ok("Mandat supprimé avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression du mandat: " + e.getMessage());
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMandatsByUser(@PathVariable Long userId) {
        try {
            List<MandatResponseDto> mandats = mandatService.getMandatsByUser(userId);
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération des mandats de l'utilisateur: " + e.getMessage());
        }
    }
    @GetMapping("/active")
    public ResponseEntity<?> getActiveMandats() {
        try {
            List<MandatResponseDto> mandats = mandatService.getActiveMandats();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération des mandats actifs: " + e.getMessage());
        }
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleMandatStatus(@PathVariable Long id, @RequestParam boolean actif) {
        try {
            // Cette méthode devra être ajoutée au service
            // MandatResponseDto mandat = mandatService.toggleMandatStatus(id, actif);
            return ResponseEntity.ok("Statut du mandat modifié avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification du statut: " + e.getMessage());
        }
    }
    @GetMapping("/type/controle")
    public ResponseEntity<?> getMandatsByType(@RequestParam boolean missionDeControle) {
        try {
            // Cette méthode devra être ajoutée au service
            // List<MandatResponseDto> mandats = mandatService.getMandatsByType(missionDeControle);
            return ResponseEntity.ok("Recherche par type à implémenter");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la recherche par type: " + e.getMessage());
        }
    }
    @GetMapping("/ville/{villeId}")
    public ResponseEntity<?> getMandatsByVille(@PathVariable Long villeId) {
        try {
            // Cette méthode devra être ajoutée au service
            // List<MandatResponseDto> mandats = mandatService.getMandatsByVille(villeId);
            return ResponseEntity.ok("Recherche par ville à implémenter");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la recherche par ville: " + e.getMessage());
        }
    }

}
