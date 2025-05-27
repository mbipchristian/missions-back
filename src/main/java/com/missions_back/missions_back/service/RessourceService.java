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
        if (ressourceRepo.existsByCode(dto.code())) {
            throw new IllegalArgumentException("Une ressource avec ce code existe déjà");
        }
        
        Ressource ressource = new Ressource();
        ressource.setCode(dto.code());
        ressource.setName(dto.name());
        ressource.setQuantite(dto.quantite());
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
    
    public Optional<RessourceResponseDto> getRessourceByCode(String code) {
        return ressourceRepo.findByCode(code)
                .map(this::mapToResponseDto);
    }
    
    public List<RessourceResponseDto> searchRessourcesByName(String name) {
        return ressourceRepo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }
    
    // public List<RessourceResponseDto> getRessourcesWithLowStock(Long seuil) {
    //     return ressourceRepo.findRessourcesWithLowStock(seuil)
    //             .stream()
    //             .map(this::mapToResponseDto)
    //             .toList();
    // }
    // Mettre à jour une ressource
    public Optional<RessourceResponseDto> updateRessource(Long id, RessourceDto dto) {
        return ressourceRepo.findById(id)
                .map(ressource -> {
                    // Vérifier si le code est modifié et s'il existe déjà
                    if (!ressource.getCode().equals(dto.code()) && 
                        ressourceRepo.existsByCode(dto.code())) {
                        throw new IllegalArgumentException("Une ressource avec ce code existe déjà");
                    }
                    
                    ressource.setCode(dto.code());
                    ressource.setName(dto.name());
                    ressource.setQuantite(dto.quantite());
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
    public Optional<RessourceResponseDto> updateQuantite(Long id, Long nouvelleQuantite) {
        return ressourceRepo.findById(id)
                .map(ressource -> {
                    ressource.setQuantite(nouvelleQuantite);
                    ressource.setUpdated_at(LocalDateTime.now());
                    return mapToResponseDto(ressourceRepo.save(ressource));
                });
    }
    public Optional<RessourceResponseDto> ajouterQuantite(Long id, Long quantiteAAjouter) {
        return ressourceRepo.findById(id)
                .map(ressource -> {
                    ressource.setQuantite(ressource.getQuantite() + quantiteAAjouter);
                    ressource.setUpdated_at(LocalDateTime.now());
                    return mapToResponseDto(ressourceRepo.save(ressource));
                });
    }
    public Optional<RessourceResponseDto> retirerQuantite(Long id, Long quantiteARetirer) {
        return ressourceRepo.findById(id)
                .map(ressource -> {
                    long nouvelleQuantite = ressource.getQuantite() - quantiteARetirer;
                    if (nouvelleQuantite < 0) {
                        throw new IllegalArgumentException("Quantité insuffisante");
                    }
                    ressource.setQuantite(nouvelleQuantite);
                    ressource.setUpdated_at(LocalDateTime.now());
                    return mapToResponseDto(ressourceRepo.save(ressource));
                });
    }
    private RessourceResponseDto mapToResponseDto(Ressource ressource) {
        return new RessourceResponseDto(
                ressource.getId(),
                ressource.getName(),
                ressource.getCode(),
                ressource.getQuantite(),
                ressource.getCreated_at(),
                ressource.getUpdated_at()
        );
    }
}
