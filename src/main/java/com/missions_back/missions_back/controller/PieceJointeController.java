package com.missions_back.missions_back.controller;

import org.springframework.http.MediaType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.missions_back.missions_back.dto.PieceJointeDto;
import com.missions_back.missions_back.dto.PieceJointeResponseDto;
import com.missions_back.missions_back.service.PieceJointeService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import java.net.URLEncoder;

@RestController
@RequestMapping("/auth/pieces-jointes")
@CrossOrigin(origins = "*")
public class PieceJointeController {

    @Autowired
    private PieceJointeService pieceJointeService;

    // ajouter une nouvelle pièce jointe
    // Contrôleur mis à jour pour gérer l'upload de fichiers
@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<PieceJointeResponseDto> uploadPieceJointe(
        @RequestParam("file") MultipartFile file,
        @RequestParam("userId") Long userId,
        @RequestParam(value = "mandatId", required = false) Long mandatId,
        @RequestParam(value = "ordreMissionId", required = false) Long ordreMissionId,
        @RequestParam(value = "rapportId", required = false) Long rapportId,
        @RequestParam(value = "description", required = false) String description) {
    
    try {
        PieceJointeResponseDto pieceJointeCreee = pieceJointeService.uploadPieceJointe(
            file, userId, mandatId, ordreMissionId, rapportId, description);
        return new ResponseEntity<>(pieceJointeCreee, HttpStatus.CREATED);
    } catch (RuntimeException e) {
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    } catch (IOException e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
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

    /**
 * Télécharger une pièce jointe par son ID
 * @param id L'identifiant de la pièce jointe
 * @return ResponseEntity contenant le fichier avec les headers appropriés
 */
@GetMapping("/{id}/download")
public ResponseEntity<Resource> telechargerPieceJointe(@PathVariable Long id) {
    try {
        // Obtenir les informations de la pièce jointe
        PieceJointeResponseDto infoPieceJointe = pieceJointeService.obtenirInfosPieceJointe(id);
        
        // Télécharger le fichier
        Resource resource = pieceJointeService.telechargerPieceJointe(id);
        
        // Encoder le nom du fichier pour éviter les problèmes d'encodage
        String nomFichierEncode = URLEncoder.encode(infoPieceJointe.getNomOriginal(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        
        // Déterminer le type MIME
        String contentType = infoPieceJointe.getTypeMime();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }
        
        // Configurer les headers de la réponse
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"" + infoPieceJointe.getNomOriginal() + "\"; filename*=UTF-8''" + nomFichierEncode)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(infoPieceJointe.getTaille()))
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(resource);
                
    } catch (EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    } catch (FileNotFoundException e) {
        return ResponseEntity.notFound().build();
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
    
}