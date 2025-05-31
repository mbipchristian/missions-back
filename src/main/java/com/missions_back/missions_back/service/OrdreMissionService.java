package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.missions_back.missions_back.dto.OrdreMissionDto;
import com.missions_back.missions_back.dto.OrdreMissionResponseDto;
import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.OrdreMission;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.OrdreMissionRepo;
import com.missions_back.missions_back.repository.UserRepo;

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
    
    public OrdreMissionResponseDto getOrdreMissionByReference(String reference) {
        OrdreMission ordreMission = ordreMissionRepo.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé avec la référence: " + reference));
        return mapToResponseDto(ordreMission);
    }
    public List<OrdreMissionResponseDto> getOrdresMissionByUserId(Long userId) {
        return ordreMissionRepo.findActiveByUserId(userId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<OrdreMissionResponseDto> getOrdresMissionByMandatId(Long mandatId) {
        return ordreMissionRepo.findActiveByMandatId(mandatId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    private void validateBusinessRules(User user, Mandat mandat, OrdreMissionDto dto) {
        Long quotaAnnuel = user.getQuotaAnnuel();
        
        // 1. Si quota annuel < 100
        if (quotaAnnuel < 100) {
            // Vérifier conflit de dates avec ordres existants
            List<OrdreMission> conflictingOrders = ordreMissionRepo
                    .findActiveByUserIdWithDateConflict(user.getId(), dto.dateDebut());
            
            if (!conflictingOrders.isEmpty()) {
                throw new RuntimeException("Il existe déjà un ordre de mission dont la date de fin chevauche avec la date de début de cette mission");
            }
            
            // Vérifier si quota + durée > 100
            Long currentTotalDuration = ordreMissionRepo.sumDureeByUserId(user.getId());
            if (currentTotalDuration == null) currentTotalDuration = 0L;
            
            if ((quotaAnnuel + dto.duree()) > 100) {
                // Vérifier si c'est une mission de contrôle
                if (!mandat.isMissionDeControle()) {
                    throw new RuntimeException("Le quota annuel sera dépassé. Seules les missions de contrôle sont autorisées");
                }
            }
        }
        // 2. Si quota annuel >= 100
        else {
            // Seules les missions de contrôle sont autorisées
            if (!mandat.isMissionDeControle()) {
                throw new RuntimeException("Quota annuel déjà atteint. Seules les missions de contrôle sont autorisées");
            }
            
            // Vérifier conflit de dates même pour missions de contrôle
            List<OrdreMission> conflictingOrders = ordreMissionRepo
                    .findActiveByUserIdWithDateConflict(user.getId(), dto.dateDebut());
            
            if (!conflictingOrders.isEmpty()) {
                throw new RuntimeException("Il existe déjà un ordre de mission dont la date de fin chevauche avec la date de début de cette mission");
            }
        }
    }
    public OrdreMissionResponseDto createOrdreMission(OrdreMissionDto ordreMissionDto, Long userId) {
        // Vérifications de base
        if (ordreMissionRepo.existsByReference(ordreMissionDto.reference())) {
            throw new RuntimeException("Un ordre de mission avec cette référence existe déjà");
        }
        
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));
        
        Mandat mandat = mandatRepo.findById(ordreMissionDto.mandatId())
                .orElseThrow(() -> new RuntimeException("Mandat non trouvé avec l'ID: " + ordreMissionDto.mandatId()));
        
        // Validation des montants et dates
        validateMontants(ordreMissionDto);        
        // Application de la logique métier complexe
        validateBusinessRules(user, mandat, ordreMissionDto);
        
        OrdreMission ordreMission = new OrdreMission();
        ordreMission.setReference(ordreMissionDto.reference());
        ordreMission.setObjectif(ordreMissionDto.objectif());
        ordreMission.setModePaiement(ordreMissionDto.modePaiement());
        ordreMission.setDevise(ordreMissionDto.devise());
        ordreMission.setTauxAvance(ordreMissionDto.tauxAvance());
        ordreMission.setDateDebut(ordreMissionDto.dateDebut());
        ordreMission.setDateFin(ordreMissionDto.dateFin());
        ordreMission.setDuree(ordreMissionDto.duree());
        ordreMission.setDecompteTotal(ordreMissionDto.decompteTotal());
        ordreMission.setDecompteAvance(ordreMissionDto.decompteAvance());
        ordreMission.setDecompteRelicat(ordreMissionDto.decompteRelicat());
        ordreMission.setUser(user);
        ordreMission.setMandat(mandat);
        ordreMission.setActif(true);

        // Mise à jour du quota annuel
        Long nouveauQuotaAnnuel = user.getQuotaAnnuel() + ordreMission.getDuree().intValue();
        user.setQuotaAnnuel(nouveauQuotaAnnuel);
        
        // Sauvegarde de l'utilisateur avec le nouveau quota
        userRepo.save(user);
        
        OrdreMission savedOrdreMission = ordreMissionRepo.save(ordreMission);
        return mapToResponseDto(savedOrdreMission);
    }
    
    public OrdreMissionResponseDto updateOrdreMission(Long id, OrdreMissionDto ordreMissionDto, Long userId) {
        OrdreMission existingOrdreMission = ordreMissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé avec l'ID: " + id));
        
        // Vérifier si la nouvelle référence n'existe pas déjà (sauf pour l'ordre actuel)
        if (!existingOrdreMission.getReference().equals(ordreMissionDto.reference()) 
            && ordreMissionRepo.existsByReference(ordreMissionDto.reference())) {
            throw new RuntimeException("Un ordre de mission avec cette référence existe déjà");
        }
        
        Mandat mandat = mandatRepo.findById(ordreMissionDto.mandatId())
                .orElseThrow(() -> new RuntimeException("Mandat non trouvé avec l'ID: " + ordreMissionDto.mandatId()));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));
        
        // Validation des montants
        validateMontants(ordreMissionDto);
        
        existingOrdreMission.setReference(ordreMissionDto.reference());
        existingOrdreMission.setObjectif(ordreMissionDto.objectif());
        existingOrdreMission.setModePaiement(ordreMissionDto.modePaiement());
        existingOrdreMission.setDevise(ordreMissionDto.devise());
        existingOrdreMission.setTauxAvance(ordreMissionDto.tauxAvance());
        existingOrdreMission.setDuree(ordreMissionDto.duree());
        existingOrdreMission.setDecompteTotal(ordreMissionDto.decompteTotal());
        existingOrdreMission.setDecompteAvance(ordreMissionDto.decompteAvance());
        existingOrdreMission.setDecompteRelicat(ordreMissionDto.decompteRelicat());
        existingOrdreMission.setMandat(mandat);
        
        OrdreMission updatedOrdreMission = ordreMissionRepo.save(existingOrdreMission);

        // Mise à jour du quota annuel
        Long nouveauQuotaAnnuel = user.getQuotaAnnuel() + updatedOrdreMission.getDuree().intValue();
        user.setQuotaAnnuel(nouveauQuotaAnnuel);
        // Sauvegarde de l'utilisateur avec le nouveau quota
        userRepo.save(user);
        
        return mapToResponseDto(updatedOrdreMission);
    }
    public void deleteOrdreMission(Long id) {
        OrdreMission ordreMission = ordreMissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé avec l'ID: " + id));
        
        ordreMission.setActif(false);
        ordreMission.setDeleted_at(LocalDateTime.now());
        ordreMissionRepo.save(ordreMission);
    }
    public Long countOrdresMissionByMandat(Long mandatId) {
        return ordreMissionRepo.countActiveByMandatId(mandatId);
    }
    private void validateMontants(OrdreMissionDto dto) {
        if (dto.tauxAvance() < 0 || dto.tauxAvance() > 100) {
            throw new RuntimeException("Le taux d'avance doit être compris entre 0 et 100");
        }
        
        if (dto.duree() <= 0) {
            throw new RuntimeException("La durée doit être positive");
        }
        
        if (dto.decompteTotal() <= 0) {
            throw new RuntimeException("Le décompte total doit être positif");
        }
        
        if (dto.decompteAvance() < 0) {
            throw new RuntimeException("Le décompte avance ne peut pas être négatif");
        }
        
        if (dto.decompteRelicat() < 0) {
            throw new RuntimeException("Le décompte reliquat ne peut pas être négatif");
        }
        
        if (dto.decompteAvance() + dto.decompteRelicat() != dto.decompteTotal()) {
            throw new RuntimeException("La somme du décompte avance et reliquat doit égaler le décompte total");
        }
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
                ordreMission.getCreated_at(),
                ordreMission.getUpdated_at()
        );
    }
}
