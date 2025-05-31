package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.RapportDto;
import com.missions_back.missions_back.dto.RapportResponseDto;
import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.Rapport;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.RapportRepo;

@Transactional
@Service
public class RapportService {
    private final MandatRepo mandatRepo;
    private final RapportRepo rapportRepo;

    public RapportService(MandatRepo mandatRepo, RapportRepo rapportRepo) {
        this.mandatRepo = mandatRepo;
        this.rapportRepo = rapportRepo;
    }
    public List<RapportResponseDto> getAllRapports() {
        return rapportRepo.findAllActive()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public RapportResponseDto getRapportById(Long id) {
        Rapport rapport = rapportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé avec l'ID: " + id));
        return mapToResponseDto(rapport);
    }
    public RapportResponseDto getRapportByReference(String reference) {
        Rapport rapport = rapportRepo.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé avec la référence: " + reference));
        return mapToResponseDto(rapport);
    }
    
    // public List<RapportResponseDto> getRapportsByUserId(Long userId) {
    //     return rapportRepo.findActiveByUserId(userId)
    //             .stream()
    //             .map(this::mapToResponseDto)
    //             .collect(Collectors.toList());
    // }
    public RapportResponseDto createRapport(RapportDto rapportDto) {
        // Vérifications
        if (rapportRepo.existsByReference(rapportDto.reference())) {
            throw new RuntimeException("Un rapport avec cette référence existe déjà");
        }
        
        if (rapportRepo.existsByMandatId(rapportDto.mandatId())) {
            throw new RuntimeException("Un rapport existe déjà pour ce mandat");
        }
        
        Mandat mandat = mandatRepo.findById(rapportDto.mandatId())
                .orElseThrow(() -> new RuntimeException("Mandat non trouvé avec l'ID: " + rapportDto.mandatId()));
        
        Rapport rapport = new Rapport();
        rapport.setReference(rapportDto.reference());
        rapport.setMandat(mandat);
        rapport.setActif(true);
        
        Rapport savedRapport = rapportRepo.save(rapport);
        return mapToResponseDto(savedRapport);
    }
    public RapportResponseDto updateRapport(Long id, RapportDto rapportDto) {
        Rapport existingRapport = rapportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé avec l'ID: " + id));
        
        // Vérifier si la nouvelle référence n'existe pas déjà (sauf pour le rapport actuel)
        if (!existingRapport.getReference().equals(rapportDto.reference()) 
            && rapportRepo.existsByReference(rapportDto.reference())) {
            throw new RuntimeException("Un rapport avec cette référence existe déjà");
        }
        
        // Vérifier si le nouveau mandat n'a pas déjà un rapport (sauf pour le rapport actuel)
        if (!existingRapport.getMandat().getId().equals(rapportDto.mandatId()) 
            && rapportRepo.existsByMandatId(rapportDto.mandatId())) {
            throw new RuntimeException("Un rapport existe déjà pour ce mandat");
        }
        
        Mandat mandat = mandatRepo.findById(rapportDto.mandatId())
                .orElseThrow(() -> new RuntimeException("Mandat non trouvé avec l'ID: " + rapportDto.mandatId()));
        
        existingRapport.setReference(rapportDto.reference());
        existingRapport.setMandat(mandat);
        
        Rapport updatedRapport = rapportRepo.save(existingRapport);
        return mapToResponseDto(updatedRapport);
    }
    public void deleteRapport(Long id) {
        Rapport rapport = rapportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé avec l'ID: " + id));
        
        rapport.setActif(false);
        rapport.setDeleted_at(LocalDateTime.now());
        rapportRepo.save(rapport);
    }
    private RapportResponseDto mapToResponseDto(Rapport rapport) {
        return new RapportResponseDto(
                rapport.getId(),
                rapport.getReference(),
                rapport.getCreated_at(),
                rapport.getUpdated_at()
        );
    }

}
