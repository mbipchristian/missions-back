package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.VilleDto;
import com.missions_back.missions_back.dto.VilleResponseDto;
import com.missions_back.missions_back.model.Ville;
import com.missions_back.missions_back.repository.VilleRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VilleService {
    private final VilleRepo villeRepo;

    public VilleService(VilleRepo villeRepo) {
        this.villeRepo = villeRepo;
    }

    // Création d'une nouvelle ville
    @Transactional
    public VilleResponseDto createVille(VilleDto villeDto) {
        // Vérifier si une ville avec ce nom ou code existe déjà
        if (villeRepo.existsByNameOrCode(villeDto.name(), villeDto.code())) {
            throw new IllegalArgumentException("Une ville avec ce nom ou ce code existe déjà");
        }

        // Créer la ville
        Ville ville = new Ville();
        ville.setName(villeDto.name());
        ville.setCode(villeDto.code());
        ville.setInterieur(villeDto.interieur());
        ville.setCreated_at(LocalDateTime.now());
        ville.setUpdated_at(LocalDateTime.now());

        Ville savedVille = villeRepo.save(ville);
        return convertToVilleResponseDto(savedVille);
    }
    // Récupérer toutes les villes
    public List<VilleResponseDto> getAllVilles() {
        List<Ville> villes = villeRepo.findAllByOrderByNameAsc();
        return villes.stream()
                .map(this::convertToVilleResponseDto)
                .collect(Collectors.toList());
    }

    // Récupérer une ville par ID
    public VilleResponseDto getVilleById(Long id) {
        Ville ville = villeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ville non trouvée avec l'ID: " + id));
        return convertToVilleResponseDto(ville);
    }
     // Mettre à jour une ville
    @Transactional
    public VilleResponseDto updateVille(Long id, VilleDto villeDto) {
        Ville ville = villeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ville non trouvée avec l'ID: " + id));

        // Mettre à jour les champs
        ville.setName(villeDto.name());
        ville.setCode(villeDto.code());
        ville.setInterieur(villeDto.interieur());
        ville.setUpdated_at(LocalDateTime.now());

        Ville updatedVille = villeRepo.save(ville);
        return convertToVilleResponseDto(updatedVille);
    }

    // Supprimer une ville
    @Transactional
    public void deleteVille(Long id) {
        Ville ville = villeRepo.findByIdAndActifTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Ville non trouvée avec l'ID: " + id));
        ville.setActif(false);
        ville.setDeleted_at(LocalDateTime.now());
        villeRepo.save(ville);
    }
    // Rechercher des villes par nom
    public List<VilleResponseDto> getVillesByName(String name) {
        List<Ville> villes = villeRepo.findByNameContainingIgnoreCase(name);
        return villes.stream()
                .map(this::convertToVilleResponseDto)
                .collect(Collectors.toList());
    }

    // Rechercher des villes par code
    public List<VilleResponseDto> getVillesByCode(String code) {
        List<Ville> villes = villeRepo.findByCodeContainingIgnoreCase(code);
        return villes.stream()
                .map(this::convertToVilleResponseDto)
                .collect(Collectors.toList());
    }

    // Méthode de conversion vers VilleResponseDto
    private VilleResponseDto convertToVilleResponseDto(Ville ville) {
        return new VilleResponseDto(
            ville.getId(),
            ville.getName(),
            ville.getCode(),
            ville.isInterieur(),
            ville.getCreated_at(),
            ville.getUpdated_at()
        );
    }
}
