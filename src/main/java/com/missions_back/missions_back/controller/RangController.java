package com.missions_back.missions_back.controller;

import com.missions_back.missions_back.dto.RangDto;
import com.missions_back.missions_back.dto.RangResponseDto;
import com.missions_back.missions_back.service.RangService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/rangs")
@CrossOrigin(origins = "*")
public class RangController {

    @Autowired
    private RangService rangService;

    // Créer un nouveau rang
    @PostMapping
    public ResponseEntity<?> createRang( @RequestBody RangDto rangDto) {
        try {
            RangResponseDto createdRang = rangService.createRang(rangDto);
            return new ResponseEntity<>(createdRang, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                new ErrorResponse(e.getMessage()), 
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Récupérer tous les rangs actifs
    @GetMapping
    public ResponseEntity<List<RangResponseDto>> getAllRangs() {
        List<RangResponseDto> rangs = rangService.getAllRangs();
        return new ResponseEntity<>(rangs, HttpStatus.OK);
    }

    // Récupérer un rang par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRangById(@PathVariable Long id) {
        Optional<RangResponseDto> rang = rangService.getRangById(id);
        if (rang.isPresent()) {
            return new ResponseEntity<>(rang.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                new ErrorResponse("Rang non trouvé avec l'ID: " + id), 
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Mettre à jour un rang
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRang(@PathVariable Long id, @RequestBody RangDto rangDto) {
        try {
            RangResponseDto updatedRang = rangService.updateRang(id, rangDto);
            return new ResponseEntity<>(updatedRang, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                new ErrorResponse(e.getMessage()), 
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Supprimer un rang (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRang(@PathVariable Long id) {
        try {
            rangService.deleteRang(id);
            return new ResponseEntity<>(
                new SuccessResponse("Rang supprimé avec succès"), 
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
    public ResponseEntity<List<RangResponseDto>> searchRangs(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String code) {
        List<RangResponseDto> rangs = rangService.searchRangs(nom, code);
        return new ResponseEntity<>(rangs, HttpStatus.OK);
    }

    // Récupérer les rangs qui ont des fonctions associées
    @GetMapping("/with-fonctions")
    public ResponseEntity<List<RangResponseDto>> getRangsWithFonctions() {
        List<RangResponseDto> rangs = rangService.getRangsWithFonctions();
        return new ResponseEntity<>(rangs, HttpStatus.OK);
    }

    // Compter les rangs actifs
    @GetMapping("/count")
    public ResponseEntity<CountResponse> countActiveRangs() {
        long count = rangService.countActiveRangs();
        return new ResponseEntity<>(new CountResponse(count), HttpStatus.OK);
    }

    // Récupérer un rang par nom
    @GetMapping("/by-nom/{nom}")
    public ResponseEntity<?> getRangByNom(@PathVariable String nom) {
        Optional<RangResponseDto> rang = rangService.getRangByNom(nom);
        if (rang.isPresent()) {
            return new ResponseEntity<>(rang.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                new ErrorResponse("Rang non trouvé avec le nom: " + nom), 
                HttpStatus.NOT_FOUND
            );
        }
    }

    // Récupérer un rang par code
    @GetMapping("/by-code/{code}")
    public ResponseEntity<?> getRangByCode(@PathVariable String code) {
        Optional<RangResponseDto> rang = rangService.getRangByCode(code);
        if (rang.isPresent()) {
            return new ResponseEntity<>(rang.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                new ErrorResponse("Rang non trouvé avec le code: " + code), 
                HttpStatus.NOT_FOUND
            );
        }
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
