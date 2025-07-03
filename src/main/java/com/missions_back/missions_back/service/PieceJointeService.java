package com.missions_back.missions_back.service;

// Imports nécessaires à ajouter
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.PieceJointeDto;
import com.missions_back.missions_back.dto.PieceJointeResponseDto;
import com.missions_back.missions_back.model.Mandat;
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
    private RapportRepo rapportRepository;

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
    
    if (rapportId != null) {
        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));
        pieceJointe.setRapport(rapport);
    }
    
    PieceJointe pieceJointeSauvegardee = pieceJointeRepository.save(pieceJointe);
    return convertirEnResponseDto(pieceJointeSauvegardee);
}

    // Obtenir toutes les pièces jointes
    public List<PieceJointeResponseDto> obtenirToutesPiecesJointes() {
        return pieceJointeRepository.findByActifTrue()
                .stream()
                .map(this::convertirEnResponseDto)
                .collect(Collectors.toList());
    }

    // Obtenir une pièce jointe par ID
    public Optional<PieceJointeResponseDto> obtenirPieceJointeParId(Long id) {
        return pieceJointeRepository.findById(id)
                .filter(pj -> pj.isActif())
                .map(this::convertirEnResponseDto);
    }

    // Obtenir les pièces jointes par mandat
    public List<PieceJointeResponseDto> obtenirPiecesJointesParMandat(Long mandatId) {
        return pieceJointeRepository.findByMandatIdAndActifTrue(mandatId)
                .stream()
                .map(this::convertirEnResponseDto)
                .collect(Collectors.toList());
    }

    // Obtenir les pièces jointes par ordre de mission
    public List<PieceJointeResponseDto> obtenirPiecesJointesParOrdreMission(Long ordreMissionId) {
        return pieceJointeRepository.findByOrdreMissionIdAndActifTrue(ordreMissionId)
                .stream()
                .map(this::convertirEnResponseDto)
                .collect(Collectors.toList());
    }

    // Obtenir les pièces jointes par rapport
    public List<PieceJointeResponseDto> obtenirPiecesJointesParRapport(Long rapportId) {
        return pieceJointeRepository.findByRapportIdAndActifTrue(rapportId)
                .stream()
                .map(this::convertirEnResponseDto)
                .collect(Collectors.toList());
    }

    // Obtenir les pièces jointes par utilisateur
    public List<PieceJointeResponseDto> obtenirPiecesJointesParUtilisateur(Long userId) {
        return pieceJointeRepository.findByUserIdAndActifTrue(userId)
                .stream()
                .map(this::convertirEnResponseDto)
                .collect(Collectors.toList());
    }

    // Mettre à jour une pièce jointe
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

    // Rechercher par nom
    public List<PieceJointeResponseDto> rechercherParNom(String nom) {
        return pieceJointeRepository.findByNomContainingIgnoreCaseAndActifTrue(nom)
                .stream()
                .map(this::convertirEnResponseDto)
                .collect(Collectors.toList());
    }

    // Obtenir les statistiques
    public long compterPiecesJointesParMandat(Long mandatId) {
        return pieceJointeRepository.countByMandatIdAndActifTrue(mandatId);
    }

    public long compterPiecesJointesParOrdreMission(Long ordreMissionId) {
        return pieceJointeRepository.countByOrdreMissionIdAndActifTrue(ordreMissionId);
    }

    public long compterPiecesJointesParRapport(Long rapportId) {
        return pieceJointeRepository.countByRapportIdAndActifTrue(rapportId);
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