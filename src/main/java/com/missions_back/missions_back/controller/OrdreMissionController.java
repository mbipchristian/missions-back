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

import com.missions_back.missions_back.dto.OrdreMissionDto;
import com.missions_back.missions_back.dto.OrdreMissionResponseDto;
import com.missions_back.missions_back.service.OrdreMissionService;

@RestController
@RequestMapping("/auth/ordres-mission")
public class OrdreMissionController {

    private final OrdreMissionService ordreMissionService;
    public OrdreMissionController(OrdreMissionService ordreMissionService) {
        this.ordreMissionService = ordreMissionService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<OrdreMissionResponseDto>> getAllOrdresMission() {
        try {
            List<OrdreMissionResponseDto> ordresMission = ordreMissionService.getAllOrdresMission();
            return ResponseEntity.ok(ordresMission);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrdreMissionResponseDto> getOrdreMissionById(@PathVariable Long id) {
        try {
            OrdreMissionResponseDto ordreMission = ordreMissionService.getOrdreMissionById(id);
            return ResponseEntity.ok(ordreMission);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrdreMissionResponseDto>> getOrdresMissionByUserId(@PathVariable Long userId) {
        try {
            List<OrdreMissionResponseDto> ordresMission = ordreMissionService.getOrdresMissionByUserId(userId);
            return ResponseEntity.ok(ordresMission);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/mandat/{mandatId}")
    public ResponseEntity<List<OrdreMissionResponseDto>> getOrdresMissionByMandatId(@PathVariable Long mandatId) {
        try {
            List<OrdreMissionResponseDto> ordresMission = ordreMissionService.getOrdresMissionByMandatId(mandatId);
            return ResponseEntity.ok(ordresMission);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/mandat/{mandatId}/count")
    public ResponseEntity<Long> countOrdresMissionByMandat(@PathVariable Long mandatId) {
        try {
            Long count = ordreMissionService.countOrdresMissionByMandat(mandatId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createOrdreMission(@PathVariable Long userId, 
                                               @RequestBody OrdreMissionDto ordreMissionDto) {
        try {
            OrdreMissionResponseDto createdOrdreMission = ordreMissionService.createOrdreMission(ordreMissionDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrdreMission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de l'ordre de mission");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrdreMission(@PathVariable Long id, 
                                                @PathVariable Long userId,  
                                               @RequestBody OrdreMissionDto ordreMissionDto) {
        try {
            OrdreMissionResponseDto updatedOrdreMission = ordreMissionService.updateOrdreMission(id, ordreMissionDto, userId);
            return ResponseEntity.ok(updatedOrdreMission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour de l'ordre de mission");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrdreMission(@PathVariable Long id) {
        try {
            ordreMissionService.deleteOrdreMission(id);
            return ResponseEntity.ok().body("Ordre de mission supprimé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression de l'ordre de mission");
        }
    }
}
