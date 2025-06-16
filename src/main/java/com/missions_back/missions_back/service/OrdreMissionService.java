package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.OrdreMissionResponseDto;
import com.missions_back.missions_back.model.OrdreMission;
import com.missions_back.missions_back.model.OrdreMissionStatut;
import com.missions_back.missions_back.model.RoleEnum;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.OrdreMissionRepo;
import com.missions_back.missions_back.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrdreMissionService {
    private final OrdreMissionRepo ordreMissionRepo;
    private final UserRepo userRepo;
    private final MandatRepo mandatRepo;

    public OrdreMissionService(OrdreMissionRepo ordreMissionRepo, UserRepo userRepo, MandatRepo mandatRepo) {
        this.ordreMissionRepo = ordreMissionRepo;
        this.userRepo = userRepo;
        this.mandatRepo = mandatRepo;
    }

    @Transactional
    public OrdreMissionResponseDto ajouterPieceJointeEtSoumettre(Long ordreMissionId, Long userId) {
        OrdreMission ordreMission = ordreMissionRepo.findById(ordreMissionId)
            .orElseThrow(() -> new EntityNotFoundException("Ordre de mission non trouvé"));

        // Vérifier que l'utilisateur a les droits (AGENT_RESSOURCES_HUMAINES)
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        
        if (!user.getRole().equals(RoleEnum.AGENT_RESSOURCES_HUMAINES) && 
            !user.getRole().equals(RoleEnum.ADMIN)) {
            throw new IllegalArgumentException("Seul l'agent des ressources humaines peut soumettre un ordre de mission");
        }

        if (ordreMission.getStatut() != OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF) {
            throw new IllegalArgumentException("Cet ordre de mission ne peut pas être soumis dans son état actuel");
        }

        // Marquer comme ayant une pièce jointe et soumettre
        ordreMission.setPieceJointeAjoutee(true);
        ordreMission.setStatut(OrdreMissionStatut.EN_ATTENTE_CONFIRMATION);
        ordreMission.setDateSoumission(LocalDateTime.now());
        
        OrdreMission updatedOrdreMission = ordreMissionRepo.save(ordreMission);
        return mapToResponseDto(updatedOrdreMission);
    }
    public List<OrdreMissionResponseDto> getOrdresMissionParMandat(Long mandatId) {
        // Vérifier que le mandat existe
        if (!mandatRepo.existsById(mandatId)) {
            throw new EntityNotFoundException("Mandat non trouvé avec l'ID: " + mandatId);
        }
        
        // Récupérer les ordres de mission du mandat
        List<OrdreMission> ordres = ordreMissionRepo.findByMandatId(mandatId);
        
        // Convertir en DTO
        return ordres.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrdreMissionResponseDto confirmerOrdreMission(Long ordreMissionId, Long userId) {
        // Vérifier que l'utilisateur a les droits (DIRECTEUR_RESSOURCES_HUMAINES)
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        
        if (!user.getRole().equals(RoleEnum.DIRECTEUR_RESSOURCES_HUMAINES) && 
            !user.getRole().equals(RoleEnum.ADMIN)) {
            throw new IllegalArgumentException("Seul le directeur des ressources humaines peut confirmer un ordre de mission");
        }

        OrdreMission ordreMission = ordreMissionRepo.findById(ordreMissionId)
            .orElseThrow(() -> new EntityNotFoundException("Ordre de mission non trouvé"));

        if (ordreMission.getStatut() != OrdreMissionStatut.EN_ATTENTE_CONFIRMATION) {
            throw new IllegalArgumentException("Cet ordre de mission ne peut pas être confirmé dans son état actuel");
        }

        // Confirmer l'ordre de mission
        ordreMission.setStatut(OrdreMissionStatut.EN_ATTENTE_EXECUTION);
        ordreMission.setConfirmeParUserId(userId);
        ordreMission.setDateConfirmation(LocalDateTime.now());
        
        OrdreMission confirmedOrdreMission = ordreMissionRepo.save(ordreMission);
        return mapToResponseDto(confirmedOrdreMission);
    }

    public List<OrdreMissionResponseDto> getOrdresMissionEnAttenteConfirmation() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatut(OrdreMissionStatut.EN_ATTENTE_CONFIRMATION);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<OrdreMissionResponseDto> getOrdresMissionVisiblesPourAgent() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatutIn(
            List.of(OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF, OrdreMissionStatut.EN_ATTENTE_CONFIRMATION)
        );
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<OrdreMissionResponseDto> getOrdresMissionPourUtilisateur(Long userId) {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByUserIdAndStatutNot(
            userId, OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void mettreAJourStatutsAutomatiquement() {
        Date maintenant = new Date();
        
        // Mettre à jour les ordres EN_ATTENTE_EXECUTION vers EN_COURS
        List<OrdreMission> ordresADemarrer = ordreMissionRepo.findByStatutAndDateDebutLessThanEqual(
            OrdreMissionStatut.EN_ATTENTE_EXECUTION, maintenant);
        
        for (OrdreMission ordre : ordresADemarrer) {
            ordre.setStatut(OrdreMissionStatut.EN_COURS);
            ordreMissionRepo.save(ordre);
        }
        
        // Mettre à jour les ordres EN_COURS vers ACHEVE
        List<OrdreMission> ordresATerminer = ordreMissionRepo.findByStatutAndDateFinLessThanEqual(
            OrdreMissionStatut.EN_COURS, maintenant);
        
        for (OrdreMission ordre : ordresATerminer) {
            ordre.setStatut(OrdreMissionStatut.ACHEVE);
            ordreMissionRepo.save(ordre);
        }
    }

    // Autres méthodes existantes...
    public List<OrdreMissionResponseDto> getAllOrdresMission() {
        return ordreMissionRepo.findAllActive()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public OrdreMissionResponseDto getOrdreMissionById(Long id) {
        OrdreMission ordreMission = ordreMissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé avec l'ID: " + id));
        return mapToResponseDto(ordreMission);
    }

    private OrdreMissionResponseDto mapToResponseDto(OrdreMission ordreMission) {
        return new OrdreMissionResponseDto(
                ordreMission.getId(),
                ordreMission.getReference(),
                ordreMission.getObjectif(),
                ordreMission.getModePaiement(),
                ordreMission.getDevise(),
                ordreMission.getDateDebut(),
                ordreMission.getDateFin(),
                ordreMission.getDuree(),
                ordreMission.getTauxAvance(),
                ordreMission.getDecompteTotal(),
                ordreMission.getDecompteAvance(),
                ordreMission.getDecompteRelicat(),
                ordreMission.getStatut(),
                ordreMission.getCreated_at(),
                ordreMission.getUpdated_at()
        );
    }
}
