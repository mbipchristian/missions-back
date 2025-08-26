package com.missions_back.missions_back.service;

// Imports nécessaires à ajouter
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.time.LocalDateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.PieceJointeDto;
import com.missions_back.missions_back.dto.PieceJointeResponseDto;
import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.MandatStatut;
import com.missions_back.missions_back.model.OrdreMission;
import com.missions_back.missions_back.model.OrdreMissionStatut;
import com.missions_back.missions_back.model.PieceJointe;
import com.missions_back.missions_back.model.Rapport;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.OrdreMissionRepo;
import com.missions_back.missions_back.repository.PieceJointeRepository;
import com.missions_back.missions_back.repository.RapportRepo;
import com.missions_back.missions_back.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Service
@Transactional
public class PieceJointeService {

    @Autowired
    private PieceJointeRepository pieceJointeRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private MandatRepo mandatRepository;

    @Autowired
    private OrdreMissionRepo ordreMissionRepository;

    @Autowired
    private EmailService emailService;

public PieceJointeResponseDto uploadPieceJointe(MultipartFile file, Long userId, 
        Long mandatId, Long ordreMissionId, Long rapportId, String description) throws IOException {
    
    // Vérifier que le fichier n'est pas vide
    if (file.isEmpty()) {
        throw new RuntimeException("Le fichier ne peut pas être vide");
    }
    
    // Vérifier que l'utilisateur existe
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    
    // Vérifier qu'au moins une association existe
    if (mandatId == null && ordreMissionId == null && rapportId == null) {
        throw new RuntimeException("La pièce jointe doit être associée à au moins un élément");
    }
    
    // Générer un nom unique pour le fichier
    String nomFichier = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    
    // Définir le chemin de stockage
    String cheminStockage = "uploads/" + nomFichier;
    
    // Sauvegarder le fichier physiquement
    Path cheminFichier = Paths.get(cheminStockage);
    Files.createDirectories(cheminFichier.getParent());
    Files.copy(file.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);
    
    // Créer l'entité PieceJointe
    PieceJointe pieceJointe = new PieceJointe();
    pieceJointe.setNom(nomFichier);
    pieceJointe.setNomOriginal(file.getOriginalFilename());
    pieceJointe.setCheminFichier(cheminStockage);
    pieceJointe.setTypeMime(file.getContentType());
    pieceJointe.setTaille(file.getSize());
    pieceJointe.setDescription(description);
    pieceJointe.setUser(user);
    
    // Associer aux entités si les IDs sont fournis
    if (mandatId != null) {
        Mandat mandat = mandatRepository.findById(mandatId)
        .orElseThrow(() -> new RuntimeException("Mandat non trouvé"));
        pieceJointe.setMandat(mandat);

        // Si le mandat est "ACHEVE", le passer à "ACHEVE_AVEC_RAPPORT"
        if (MandatStatut.ACHEVE.equals(mandat.getStatut())) {
            mandat.setStatut(MandatStatut.ACHEVE_AVEC_RAPPORT);
            mandat.setUpdated_at(LocalDateTime.now());
            mandatRepository.save(mandat);

            // Notifier les utilisateurs du mandat
    //         if (mandat.getUsers() != null && !mandat.getUsers().isEmpty()) {
    //         emailService.sendEmail(
    //             mandat.getUsers().stream().map(User::getEmail).toList(),
    //             "Mandat confirmé",
    //             "Votre mandat " + mandat.getReference() + " a été mis au statut ACHEVE AVEC RAPPORT"
    //         );
    // }
        }
        // Si le statut est "EN_ATTENTE_CONFIRMATION", aucune modification nécessaire
    }
    
    if (ordreMissionId != null) {
        OrdreMission ordreMission = ordreMissionRepository.findById(ordreMissionId)
                .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé"));
        pieceJointe.setOrdreMission(ordreMission);
        // MISE À JOUR DU STATUT : Changer le statut en "EN_ATTENTE_CONFIRMATION"
        ordreMission.setStatut(OrdreMissionStatut.EN_ATTENTE_CONFIRMATION);
        ordreMission.setUpdated_at(LocalDateTime.now());
        ordreMissionRepository.save(ordreMission);
    }
    
    PieceJointe pieceJointeSauvegardee = pieceJointeRepository.save(pieceJointe);
    return convertirEnResponseDto(pieceJointeSauvegardee);
}

// Ajouter cette méthode dans la classe PieceJointeService

/**
 * Télécharger une pièce jointe par son ID
 * @param id L'identifiant de la pièce jointe
 * @return Resource contenant le fichier
 * @throws IOException En cas d'erreur de lecture du fichier
 */
public Resource telechargerPieceJointe(Long id) throws IOException {
    // Récupérer la pièce jointe depuis la base de données
    PieceJointe pieceJointe = pieceJointeRepository.findById(id)
            .filter(pj -> pj.isActif())
            .orElseThrow(() -> new EntityNotFoundException("Pièce jointe non trouvée avec l'ID : " + id));
    
    // Construire le chemin vers le fichier
    Path cheminFichier = Paths.get(pieceJointe.getCheminFichier());
    
    // Vérifier que le fichier existe physiquement
    if (!Files.exists(cheminFichier)) {
        throw new FileNotFoundException("Le fichier physique n'existe pas : " + pieceJointe.getCheminFichier());
    }
    
    // Vérifier que le fichier est lisible
    if (!Files.isReadable(cheminFichier)) {
        throw new IOException("Le fichier n'est pas lisible : " + pieceJointe.getCheminFichier());
    }
    
    try {
        // Créer une ressource à partir du fichier - Utiliser FileSystemResource au lieu de UrlResource
        Resource resource = new FileSystemResource(cheminFichier);
        
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("Impossible de lire le fichier : " + pieceJointe.getNomOriginal());
        }
    } catch (Exception e) {
        throw new IOException("Erreur lors de la création de la ressource fichier", e);
    }
}

/**
 * Obtenir les informations d'une pièce jointe pour le téléchargement
 * @param id L'identifiant de la pièce jointe
 * @return PieceJointeResponseDto contenant les informations
 */
public PieceJointeResponseDto obtenirInfosPieceJointe(Long id) {
    PieceJointe pieceJointe = pieceJointeRepository.findById(id)
            .filter(pj -> pj.isActif())
            .orElseThrow(() -> new RuntimeException("Pièce jointe non trouvée"));
    
    return convertirEnResponseDto(pieceJointe);
}

    
    public PieceJointeResponseDto mettreAJourPieceJointe(Long id, PieceJointeDto pieceJointeDto) {
        PieceJointe pieceJointe = pieceJointeRepository.findById(id)
                .filter(pj -> pj.isActif())
                .orElseThrow(() -> new RuntimeException("Pièce jointe non trouvée"));

        // Mettre à jour les champs modifiables
        pieceJointe.setNom(pieceJointeDto.getNom());
        pieceJointe.setDescription(pieceJointeDto.getDescription());

        PieceJointe pieceJointeMiseAJour = pieceJointeRepository.save(pieceJointe);
        return convertirEnResponseDto(pieceJointeMiseAJour);
    }

    // Supprimer une pièce jointe (soft delete)
    public void supprimerPieceJointe(Long id) {
        PieceJointe pieceJointe = pieceJointeRepository.findById(id)
                .filter(pj -> pj.isActif())
                .orElseThrow(() -> new RuntimeException("Pièce jointe non trouvée"));

        pieceJointe.setActif(false);
        pieceJointe.setDeleted_at(LocalDateTime.now());
        pieceJointeRepository.save(pieceJointe);
    }


    // Méthode utilitaire pour convertir en DTO de réponse
    private PieceJointeResponseDto convertirEnResponseDto(PieceJointe pieceJointe) {
        PieceJointeResponseDto dto = new PieceJointeResponseDto();
        dto.setId(pieceJointe.getId());
        dto.setNom(pieceJointe.getNom());
        dto.setNomOriginal(pieceJointe.getNomOriginal());
        dto.setCheminFichier(pieceJointe.getCheminFichier());
        dto.setTypeMime(pieceJointe.getTypeMime());
        dto.setTaille(pieceJointe.getTaille());
        dto.setDescription(pieceJointe.getDescription());
        dto.setCreated_at(pieceJointe.getCreated_at());
        dto.setUpdated_at(pieceJointe.getUpdated_at());
        dto.setActif(pieceJointe.isActif());

        // Relations
        if (pieceJointe.getUser() != null) {
            dto.setUserId(pieceJointe.getUser().getId());
            // Se rassurer que la classe User a une méthode getName()
            dto.setUserName(pieceJointe.getUser().getName());
        }

        if (pieceJointe.getMandat() != null) {
            dto.setMandatId(pieceJointe.getMandat().getId());
            dto.setMandatReference(pieceJointe.getMandat().getReference());
        }

        if (pieceJointe.getOrdreMission() != null) {
            dto.setOrdreMissionId(pieceJointe.getOrdreMission().getId());
            dto.setOrdreMissionReference(pieceJointe.getOrdreMission().getReference());
        }

        if (pieceJointe.getRapport() != null) {
            dto.setRapportId(pieceJointe.getRapport().getId());
            dto.setRapportReference(pieceJointe.getRapport().getReference());
        }

        return dto;
    }
}