package com.missions_back.missions_back.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.missions_back.missions_back.dto.EtapeDto;
import com.missions_back.missions_back.dto.EtapeResponseDto;
import com.missions_back.missions_back.service.EtapeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/etapes")
@CrossOrigin(origins = "*")
public class EtapeController {
    
    @Autowired
    private EtapeService etapeService;
    
    /**
     * Récupérer toutes les étapes d'un mandat
     */
    @GetMapping("/mandat/{mandatId}")
    public ResponseEntity<List<EtapeResponseDto>> getEtapesByMandatId(@PathVariable Long mandatId) {
        List<EtapeResponseDto> etapes = etapeService.getAllEtapesByMandatId(mandatId);
        return ResponseEntity.ok(etapes);
    }
    
    /**
     * Récupérer une étape par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EtapeResponseDto> getEtapeById(@PathVariable Long id) {
        EtapeResponseDto etape = etapeService.getEtapeById(id);
        return ResponseEntity.ok(etape);
    }
    
    /**
     * Créer une nouvelle étape
     */
    @PostMapping("/create")
    public ResponseEntity<EtapeResponseDto> createEtape(@Valid @RequestBody EtapeDto etapeDto) {
        EtapeResponseDto createdEtape = etapeService.createEtape(etapeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEtape);
    }
    
    /**
     * Mettre à jour une étape
     */
    @PutMapping("/{id}")
    public ResponseEntity<EtapeResponseDto> updateEtape(@PathVariable Long id, 
                                                        @Valid @RequestBody EtapeDto etapeDto) {
        EtapeResponseDto updatedEtape = etapeService.updateEtape(id, etapeDto);
        return ResponseEntity.ok(updatedEtape);
    }
    
    /**
     * Supprimer une étape (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtape(@PathVariable Long id) {
        etapeService.deleteEtape(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Récupérer l'étape suivante dans l'ordre
     */
    @GetMapping("/mandat/{mandatId}/next/{currentOrdre}")
    public ResponseEntity<EtapeResponseDto> getNextEtape(@PathVariable Long mandatId, 
                                                         @PathVariable int currentOrdre) {
        Optional<EtapeResponseDto> nextEtape = etapeService.getNextEtape(mandatId, currentOrdre);
        return nextEtape.map(etape -> ResponseEntity.ok(etape))
                       .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Récupérer l'étape précédente dans l'ordre
     */
    @GetMapping("/mandat/{mandatId}/previous/{currentOrdre}")
    public ResponseEntity<EtapeResponseDto> getPreviousEtape(@PathVariable Long mandatId, 
                                                             @PathVariable int currentOrdre) {
        Optional<EtapeResponseDto> previousEtape = etapeService.getPreviousEtape(mandatId, currentOrdre);
        return previousEtape.map(etape -> ResponseEntity.ok(etape))
                           .orElse(ResponseEntity.notFound().build());
    }
    
}