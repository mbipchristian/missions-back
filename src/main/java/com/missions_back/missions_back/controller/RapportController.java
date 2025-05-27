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

import com.missions_back.missions_back.dto.RapportDto;
import com.missions_back.missions_back.dto.RapportResponseDto;
import com.missions_back.missions_back.service.RapportService;

@RestController
@RequestMapping("/rapports")
public class RapportController {

    private final RapportService rapportService;
    public RapportController(RapportService rapportService) {
        this.rapportService = rapportService;
    }
    @GetMapping
    public ResponseEntity<List<RapportResponseDto>> getAllRapports() {
        try {
            List<RapportResponseDto> rapports = rapportService.getAllRapports();
            return ResponseEntity.ok(rapports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RapportResponseDto> getRapportById(@PathVariable Long id) {
        try {
            RapportResponseDto rapport = rapportService.getRapportById(id);
            return ResponseEntity.ok(rapport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/reference/{reference}")
    public ResponseEntity<RapportResponseDto> getRapportByReference(@PathVariable String reference) {
        try {
            RapportResponseDto rapport = rapportService.getRapportByReference(reference);
            return ResponseEntity.ok(rapport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/create")
    public ResponseEntity<?> createRapport(@RequestBody RapportDto rapportDto) {
        try {
            RapportResponseDto createdRapport = rapportService.createRapport(rapportDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRapport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création du rapport");
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRapport(@PathVariable Long id, 
                                          @RequestBody RapportDto rapportDto) {
        try {
            RapportResponseDto updatedRapport = rapportService.updateRapport(id, rapportDto);
            return ResponseEntity.ok(updatedRapport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour du rapport");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRapport(@PathVariable Long id) {
        try {
            rapportService.deleteRapport(id);
            return ResponseEntity.ok().body("Rapport supprimé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du rapport");
        }
    }
}
