package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.OrdreMissionResponseDto;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.UserRepo;
import com.missions_back.missions_back.service.OrdreMissionService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/auth/ordres-mission")
public class OrdreMissionController {

    private final OrdreMissionService ordreMissionService;
    
    public OrdreMissionController(OrdreMissionService ordreMissionService) {
        this.ordreMissionService = ordreMissionService;
    }
    @Autowired
    private UserRepo userRepo;

    /**
     * Récupérer tous les ordres de missions en attente de justificatif
     */
    @GetMapping("/en-attente-justificatif")
    public ResponseEntity<?> getOrdresEnAttenteJustificatif() {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionEnAttenteJustificatif();
            return ResponseEntity.ok(ordres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des ordres en attente de justificatif"));
        }
    }

    /**
     * Récupérer tous les ordres de missions en attente d'exécution
     */
    @GetMapping("/en-attente-execution")
    public ResponseEntity<?> getOrdresEnAttenteExecution() {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionEnAttenteExecution();
            return ResponseEntity.ok(ordres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des ordres en attente d'exécution"));
        }
    }

    /**
     * Récupérer tous les ordres de missions en cours
     */
    @GetMapping("/en-cours")
    public ResponseEntity<?> getOrdresEnCours() {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionEnCours();
            return ResponseEntity.ok(ordres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des ordres en cours"));
        }
    }

    /**
     * Récupérer tous les ordres de missions achevés
     */
    @GetMapping("/acheves")
    public ResponseEntity<?> getOrdresAcheves() {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionAcheves();
            return ResponseEntity.ok(ordres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des ordres achevés"));
        }
    }

    @PostMapping("/{id}/ajouter-piece-jointe")
    public ResponseEntity<?> ajouterPieceJointeEtSoumettre(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            OrdreMissionResponseDto ordre = ordreMissionService.ajouterPieceJointeEtSoumettre(id, userId);
            return ResponseEntity.ok(ordre);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/confirmer")
    public ResponseEntity<?> confirmerOrdreMission(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            OrdreMissionResponseDto ordre = ordreMissionService.confirmerOrdreMission(id, userId);
            return ResponseEntity.ok(ordre);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/en-attente-confirmation")
    public ResponseEntity<List<OrdreMissionResponseDto>> getOrdresEnAttenteConfirmation() {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionEnAttenteConfirmation();
            return ResponseEntity.ok(ordres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pour-agent")
    public ResponseEntity<List<OrdreMissionResponseDto>> getOrdresPourAgent() {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionVisiblesPourAgent();
            return ResponseEntity.ok(ordres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/mes-ordres")
    public ResponseEntity<List<OrdreMissionResponseDto>> getMesOrdres(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionPourUtilisateur(userId);
            return ResponseEntity.ok(ordres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrdreMissionResponseDto>> getAllOrdresMission() {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getAllOrdresMission();
            return ResponseEntity.ok(ordres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // NOUVEAU: Récupérer les ordres de mission par utilisateur spécifique
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getOrdresMissionByUser(@PathVariable Long userId) {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionPourUtilisateur(userId);
            return ResponseEntity.ok(ordres);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Utilisateur non trouvé avec l'ID: " + userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des ordres de mission: " + e.getMessage()));
        }
    }

    // NOUVEAU: Récupérer les ordres de mission par mandat spécifique
    @GetMapping("/mandat/{mandatId}")
    public ResponseEntity<?> getOrdresMissionByMandat(@PathVariable Long mandatId) {
        try {
            List<OrdreMissionResponseDto> ordres = ordreMissionService.getOrdresMissionParMandat(mandatId);
            return ResponseEntity.ok(ordres);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Mandat non trouvé avec l'ID: " + mandatId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des ordres de mission: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdreMissionResponseDto> getOrdreMissionById(@PathVariable Long id) {
        try {
            OrdreMissionResponseDto ordre = ordreMissionService.getOrdreMissionById(id);
            return ResponseEntity.ok(ordre);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
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

    // Classe interne pour les réponses d'erreur
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
}