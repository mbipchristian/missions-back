package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.RessourceDto;
import com.missions_back.missions_back.dto.RessourceResponseDto;
import com.missions_back.missions_back.model.Ressource;
import com.missions_back.missions_back.repository.RessourceRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RessourceService {
    private final RessourceRepo ressourceRepo;

    public RessourceService(RessourceRepo ressourceRepo) {
        this.ressourceRepo = ressourceRepo;
    }
    public RessourceResponseDto createRessource(RessourceDto dto) {
        if (ressourceRepo.existsByName(dto.name())) {
            throw new IllegalArgumentException("Une ressource avec ce code existe déjà");
        }
        
        Ressource ressource = new Ressource();
        ressource.setName(dto.name());
        ressource.setCreated_at(LocalDateTime.now());
        ressource.setUpdated_at(LocalDateTime.now());
        
        return mapToResponseDto(ressourceRepo.save(ressource));
    }
    public List<RessourceResponseDto> getAllRessources() {
        return ressourceRepo.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }
    
    public Optional<RessourceResponseDto> getRessourceById(Long id) {
        return ressourceRepo.findById(id)
                .map(this::mapToResponseDto);
    }
    
    public Optional<RessourceResponseDto> getRessourceByCode(String name) {
        return ressourceRepo.findByName(name)
                .map(this::mapToResponseDto);
    }
    
    public List<RessourceResponseDto> searchRessourcesByName(String name) {
        return ressourceRepo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }
    
    public Optional<RessourceResponseDto> updateRessource(Long id, RessourceDto dto) {
        return ressourceRepo.findById(id)
                .map(ressource -> {
                    // Vérifier si le code est modifié et s'il existe déjà
                    if (!ressource.getName().equals(dto.name()) && 
                        ressourceRepo.existsByName(dto.name())) {
                        throw new IllegalArgumentException("Une ressource avec ce code existe déjà");
                    }
                    
                    ressource.setName(dto.name());
                    ressource.setUpdated_at(LocalDateTime.now());
                    
                    return mapToResponseDto(ressourceRepo.save(ressource));
                });
    }
    // Supprime logiquement une ressource
    public void deleteRessource(Long id) {
        Ressource ressource = ressourceRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        ressource.setActif(false);
        ressource.setDeleted_at(LocalDateTime.now());
        ressourceRepo.save(ressource);
    }
    
    private RessourceResponseDto mapToResponseDto(Ressource ressource) {
        return new RessourceResponseDto(
                ressource.getId(),
                ressource.getName(),
                ressource.getCreated_at(),
                ressource.getUpdated_at()
        );
    }
}
