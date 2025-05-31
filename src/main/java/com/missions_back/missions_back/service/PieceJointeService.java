package com.missions_back.missions_back.service;

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

    // Créer une pièce jointe
    public PieceJointeResponseDto creerPieceJointe(PieceJointeDto pieceJointeDto) {
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(pieceJointeDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier qu'au moins une association existe
        if (pieceJointeDto.getMandatId() == null && 
            pieceJointeDto.getOrdreMissionId() == null && 
            pieceJointeDto.getRapportId() == null) {
            throw new RuntimeException("La pièce jointe doit être associée à au moins un élément (mandat, ordre de mission ou rapport)");
        }

        PieceJointe pieceJointe = new PieceJointe();
        pieceJointe.setNom(pieceJointeDto.getNom());
        pieceJointe.setNomOriginal(pieceJointeDto.getNomOriginal());
        pieceJointe.setCheminFichier(pieceJointeDto.getCheminFichier());
        pieceJointe.setTypeMime(pieceJointeDto.getTypeMime());
        pieceJointe.setTaille(pieceJointeDto.getTaille());
        pieceJointe.setDescription(pieceJointeDto.getDescription());
        pieceJointe.setUser(user);

        // Associer aux entités si les IDs sont fournis
        if (pieceJointeDto.getMandatId() != null) {
            Mandat mandat = mandatRepository.findById(pieceJointeDto.getMandatId())
                    .orElseThrow(() -> new RuntimeException("Mandat non trouvé"));
            pieceJointe.setMandat(mandat);
        }

        if (pieceJointeDto.getOrdreMissionId() != null) {
            OrdreMission ordreMission = ordreMissionRepository.findById(pieceJointeDto.getOrdreMissionId())
                    .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé"));
            pieceJointe.setOrdreMission(ordreMission);
        }

        if (pieceJointeDto.getRapportId() != null) {
            Rapport rapport = rapportRepository.findById(pieceJointeDto.getRapportId())
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