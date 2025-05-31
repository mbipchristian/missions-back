package com.missions_back.missions_back.controller;

import com.missions_back.missions_back.dto.FonctionDto;
import com.missions_back.missions_back.dto.FonctionResponseDto;
import com.missions_back.missions_back.service.FonctionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/fonctions")
@CrossOrigin(origins = "*")
public class FonctionController {

    @Autowired
    private FonctionService fonctionService;

    // Créer une nouvelle fonction
    @PostMapping
    public ResponseEntity<?> createFonction( @RequestBody FonctionDto fonctionDto) {
        try {
            FonctionResponseDto createdFonction = fonctionService.createFonction(fonctionDto);
            return new ResponseEntity<>(createdFonction, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                new ErrorResponse(e.getMessage()), 
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Récupérer toutes les fonctions actives
    @GetMapping
    public ResponseEntity<List<FonctionResponseDto>> getAllFonctions() {
        List<FonctionResponseDto> fonctions = fonctionService.getAllFonctions();
        return new ResponseEntity<>(fonctions, HttpStatus.OK);
    }

    // Récupérer une fonction par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getFonctionById(@PathVariable Long id) {
        Optional<FonctionResponseDto> fonction = fonctionService.getFonctionById(id);
        if (fonction.isPresent()) {
            return new ResponseEntity<>(fonction.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                new ErrorResponse("Fonction non trouvée avec l'ID: " + id), 
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Mettre à jour une fonction
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFonction(@PathVariable Long id, @RequestBody FonctionDto fonctionDto) {
        try {
            FonctionResponseDto updatedFonction = fonctionService.updateFonction(id, fonctionDto);
            return new ResponseEntity<>(updatedFonction, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                new ErrorResponse(e.getMessage()), 
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Supprimer une fonction (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFonction(@PathVariable Long id) {
        try {
            fonctionService.deleteFonction(id);
            return new ResponseEntity<>(
                new SuccessResponse("Fonction supprimée avec succès"), 
                HttpStatus.OK
            );
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                new ErrorResponse(e.getMessage()), 
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Recherche avec critères
    @GetMapping("/search")
    public ResponseEntity<List<FonctionResponseDto>> searchFonctions(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) Long rangId) {
        List<FonctionResponseDto> fonctions = fonctionService.searchFonctions(nom, rangId);
        return new ResponseEntity<>(fonctions, HttpStatus.OK);
    }

    // Récupérer les fonctions par rang
    @GetMapping("/by-rang/{rangId}")
    public ResponseEntity<List<FonctionResponseDto>> getFonctionsByRang(@PathVariable Long rangId) {
        List<FonctionResponseDto> fonctions = fonctionService.getFonctionsByRang(rangId);
        return new ResponseEntity<>(fonctions, HttpStatus.OK);
    }

    // Compter les fonctions actives
    @GetMapping("/count")
    public ResponseEntity<CountResponse> countActiveFonctions() {
        long count = fonctionService.countActiveFonctions();
        return new ResponseEntity<>(new CountResponse(count), HttpStatus.OK);
    }

    // Classes internes pour les réponses
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    public static class SuccessResponse {
        private String message;
        private long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    public static class CountResponse {
        private long count;

        public CountResponse(long count) {
            this.count = count;
        }

        // Getter
        public long getCount() { return count; }
    }
}