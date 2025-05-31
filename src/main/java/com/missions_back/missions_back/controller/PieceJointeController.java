package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.missions_back.missions_back.dto.PieceJointeDto;
import com.missions_back.missions_back.dto.PieceJointeResponseDto;
import com.missions_back.missions_back.service.PieceJointeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/pieces-jointes")
@CrossOrigin(origins = "*")
public class PieceJointeController {

    @Autowired
    private PieceJointeService pieceJointeService;

    // Créer une nouvelle pièce jointe
    @PostMapping
    public ResponseEntity<PieceJointeResponseDto> creerPieceJointe(@Valid @RequestBody PieceJointeDto pieceJointeDto) {
        try {
            PieceJointeResponseDto pieceJointeCreee = pieceJointeService.creerPieceJointe(pieceJointeDto);
            return new ResponseEntity<>(pieceJointeCreee, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Obtenir toutes les pièces jointes
    @GetMapping
    public ResponseEntity<List<PieceJointeResponseDto>> obtenirToutesPiecesJointes() {
        List<PieceJointeResponseDto> piecesJointes = pieceJointeService.obtenirToutesPiecesJointes();
        return new ResponseEntity<>(piecesJointes, HttpStatus.OK);
    }

    // Obtenir une pièce jointe par ID
    @GetMapping("/{id}")
    public ResponseEntity<PieceJointeResponseDto> obtenirPieceJointeParId(@PathVariable Long id) {
        return pieceJointeService.obtenirPieceJointeParId(id)
                .map(pieceJointe -> new ResponseEntity<>(pieceJointe, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Obtenir les pièces jointes par mandat
    @GetMapping("/mandat/{mandatId}")
    public ResponseEntity<List<PieceJointeResponseDto>> obtenirPiecesJointesParMandat(@PathVariable Long mandatId) {
        List<PieceJointeResponseDto> piecesJointes = pieceJointeService.obtenirPiecesJointesParMandat(mandatId);
        return new ResponseEntity<>(piecesJointes, HttpStatus.OK);
    }

    // Obtenir les pièces jointes par ordre de mission
    @GetMapping("/ordre-mission/{ordreMissionId}")
    public ResponseEntity<List<PieceJointeResponseDto>> obtenirPiecesJointesParOrdreMission(@PathVariable Long ordreMissionId) {
        List<PieceJointeResponseDto> piecesJointes = pieceJointeService.obtenirPiecesJointesParOrdreMission(ordreMissionId);
        return new ResponseEntity<>(piecesJointes, HttpStatus.OK);
    }

    // Obtenir les pièces jointes par rapport
    @GetMapping("/rapport/{rapportId}")
    public ResponseEntity<List<PieceJointeResponseDto>> obtenirPiecesJointesParRapport(@PathVariable Long rapportId) {
        List<PieceJointeResponseDto> piecesJointes = pieceJointeService.obtenirPiecesJointesParRapport(rapportId);
        return new ResponseEntity<>(piecesJointes, HttpStatus.OK);
    }

    // Obtenir les pièces jointes par utilisateur
    @GetMapping("/utilisateur/{userId}")
    public ResponseEntity<List<PieceJointeResponseDto>> obtenirPiecesJointesParUtilisateur(@PathVariable Long userId) {
        List<PieceJointeResponseDto> piecesJointes = pieceJointeService.obtenirPiecesJointesParUtilisateur(userId);
        return new ResponseEntity<>(piecesJointes, HttpStatus.OK);
    }

    // Rechercher des pièces jointes par nom
    @GetMapping("/recherche")
    public ResponseEntity<List<PieceJointeResponseDto>> rechercherPiecesJointes(@RequestParam String nom) {
        List<PieceJointeResponseDto> piecesJointes = pieceJointeService.rechercherParNom(nom);
        return new ResponseEntity<>(piecesJointes, HttpStatus.OK);
    }

    // Mettre à jour une pièce jointe
    @PutMapping("/{id}")
    public ResponseEntity<PieceJointeResponseDto> mettreAJourPieceJointe(
            @PathVariable Long id, 
            @Valid @RequestBody PieceJointeDto pieceJointeDto) {
        try {
            PieceJointeResponseDto pieceJointeMiseAJour = pieceJointeService.mettreAJourPieceJointe(id, pieceJointeDto);
            return new ResponseEntity<>(pieceJointeMiseAJour, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer une pièce jointe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPieceJointe(@PathVariable Long id) {
        try {
            pieceJointeService.supprimerPieceJointe(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoints pour les statistiques
    @GetMapping("/stats/mandat/{mandatId}/count")
    public ResponseEntity<Long> compterPiecesJointesParMandat(@PathVariable Long mandatId) {
        long count = pieceJointeService.compterPiecesJointesParMandat(mandatId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/stats/ordre-mission/{ordreMissionId}/count")
    public ResponseEntity<Long> compterPiecesJointesParOrdreMission(@PathVariable Long ordreMissionId) {
        long count = pieceJointeService.compterPiecesJointesParOrdreMission(ordreMissionId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/stats/rapport/{rapportId}/count")
    public ResponseEntity<Long> compterPiecesJointesParRapport(@PathVariable Long rapportId) {
        long count = pieceJointeService.compterPiecesJointesParRapport(rapportId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}