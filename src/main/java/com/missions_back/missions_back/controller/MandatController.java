package com.missions_back.missions_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.missions_back.missions_back.dto.MandatDto;
import com.missions_back.missions_back.dto.MandatResponseDto;
import com.missions_back.missions_back.service.MandatService;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/auth/mandats")
@CrossOrigin(origins = "*")
public class MandatController {

    @Autowired
    private MandatService mandatService;
    
    @Autowired
    private UserRepo userRepo;

    /**
     * Récupérer tous les mandats en attente d'exécution
     */
    @GetMapping("/en-attente-execution")
    public ResponseEntity<?> getMandatsEnAttenteExecution() {
        try {
            List<MandatResponseDto> mandats = mandatService.getMandatsEnAttenteExecution();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des mandats en attente d'exécution"));
        }
    }

    /**
     * Récupérer tous les mandats en cours
     */
    @GetMapping("/en-cours")
    public ResponseEntity<?> getMandatsEnCours() {
        try {
            List<MandatResponseDto> mandats = mandatService.getMandatsEnCours();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des mandats en cours"));
        }
    }

    /**
     * Récupérer tous les mandats achevés
     */
    @GetMapping("/acheves")
    public ResponseEntity<?> getMandatsAcheves() {
        try {
            List<MandatResponseDto> mandats = mandatService.getMandatsAcheves();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des mandats achevés"));
        }
    }

    @GetMapping("/achevesAvecRapport")
    public ResponseEntity<?> getMandatsAchevesAvecRapport() {
        try {
            List<MandatResponseDto> mandats = mandatService.getMandatsAchevesAvecRapport();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des mandats achevés avec rapport"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMandat(@RequestBody MandatDto mandatDto, Authentication authentication) {
        try {
            // Récupérer automatiquement l'ID de l'utilisateur connecté
            Long createdByUserId = getUserIdFromAuthentication(authentication);
            
            // Créer le mandat avec l'ID du créateur
            MandatResponseDto createdMandat = mandatService.createMandat(mandatDto, createdByUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMandat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    

    @PostMapping("/{id}/confirmer")
public ResponseEntity<?> confirmerMandat(@PathVariable Long id, Authentication authentication) {
    try {
        MandatResponseDto confirmedMandat = mandatService.confirmerMandat(id, authentication);
        return ResponseEntity.ok(confirmedMandat);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage()));
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erreur interne du serveur"));
    }
}

    // @PostMapping("/{id}/rejeter")
    // public ResponseEntity<?> rejeterMandat(@PathVariable Long id, Authentication authentication) {
    //     try {
    //         Long userId = getUserIdFromAuthentication(authentication);
    //         mandatService.rejeterMandat(id, userId);
    //         return ResponseEntity.ok().body(new SuccessResponse("Mandat rejeté avec succès"));
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //                 .body(new ErrorResponse(e.getMessage()));
    //     } catch (EntityNotFoundException e) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body(new ErrorResponse(e.getMessage()));
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(new ErrorResponse("Erreur interne du serveur"));
    //     }
    // }

    @GetMapping("/en-attente-confirmation")
    public ResponseEntity<?> getMandatsEnAttenteConfirmation() {
        try {
            List<MandatResponseDto> mandats = mandatService.getMandatsEnAttenteConfirmation();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des mandats"));
        }
    }

    @GetMapping("/mes-mandats")
    public ResponseEntity<?> getMesMandats(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<MandatResponseDto> mandats = mandatService.getMandatsVisiblesPourUtilisateur(userId);
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des mandats"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMandats() {
        try {
            List<MandatResponseDto> mandats = mandatService.getAllMandats();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des mandats"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMandatById(@PathVariable Long id) {
        try {
            MandatResponseDto mandat = mandatService.getMandatById(id);
            return ResponseEntity.ok(mandat);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Mandat non trouvé"));
        }
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
    if (authentication == null) {
        throw new RuntimeException("Aucune authentification trouvée");
    }
    
    if (!authentication.isAuthenticated()) {
        throw new RuntimeException("Utilisateur non authentifié");
    }
    
    try {
        String username = (authentication.getPrincipal() instanceof UserDetails) 
            ? ((UserDetails) authentication.getPrincipal()).getUsername()
            : authentication.getName();
        
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Nom d'utilisateur non trouvé dans l'authentification");
        }
        
        User user = userRepo.findByUsername(username)
                .or(() -> userRepo.findByEmail(username))
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé: " + username));
        
        return user.getId();
    } catch (Exception e) {
        throw new RuntimeException("Impossible de récupérer l'ID utilisateur: " + e.getMessage(), e);
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

        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}

