package com.missions_back.missions_back.service;

import com.missions_back.missions_back.dto.FonctionDto;
import com.missions_back.missions_back.dto.FonctionResponseDto;
import com.missions_back.missions_back.model.Fonction;
import com.missions_back.missions_back.model.Rang;
import com.missions_back.missions_back.repository.FonctionRepository;
import com.missions_back.missions_back.repository.RangRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FonctionService {

    @Autowired
    private FonctionRepository fonctionRepository;

    @Autowired
    private RangRepository rangRepository;

    // Créer une nouvelle fonction
    public FonctionResponseDto createFonction(FonctionDto fonctionDto) {
        // Vérifier si le nom existe déjà
        if (fonctionRepository.existsByNomAndActifTrue(fonctionDto.getNom())) {
            throw new RuntimeException("Une fonction avec ce nom existe déjà");
        }

        // Vérifier si le rang existe
        Rang rang = rangRepository.findById(fonctionDto.getRangId())
                .orElseThrow(() -> new RuntimeException("Rang non trouvé avec l'ID: " + fonctionDto.getRangId()));

        if (!rang.isActif()) {
            throw new RuntimeException("Le rang spécifié n'est pas actif");
        }

        // Créer la fonction
        Fonction fonction = new Fonction();
        fonction.setNom(fonctionDto.getNom());
        fonction.setRang(rang);

        fonction = fonctionRepository.save(fonction);
        return mapToResponseDto(fonction);
    }

    // Récupérer toutes les fonctions actives
    @Transactional(readOnly = true)
    public List<FonctionResponseDto> getAllFonctions() {
        return fonctionRepository.findAll().stream()
                .filter(Fonction::isActif)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Récupérer une fonction par ID
    @Transactional(readOnly = true)
    public Optional<FonctionResponseDto> getFonctionById(Long id) {
        return fonctionRepository.findById(id)
                .filter(Fonction::isActif)
                .map(this::mapToResponseDto);
    }

    // Mettre à jour une fonction
    public FonctionResponseDto updateFonction(Long id, FonctionDto fonctionDto) {
        Fonction fonction = fonctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fonction non trouvée avec l'ID: " + id));

        if (!fonction.isActif()) {
            throw new RuntimeException("Cette fonction n'est pas active");
        }

        // Vérifier si le nouveau nom existe déjà (excluant la fonction actuelle)
        if (fonctionRepository.existsByNomAndActifTrueAndIdNot(fonctionDto.getNom(), id)) {
            throw new RuntimeException("Une autre fonction avec ce nom existe déjà");
        }

        // Vérifier si le rang existe
        Rang rang = rangRepository.findById(fonctionDto.getRangId())
                .orElseThrow(() -> new RuntimeException("Rang non trouvé avec l'ID: " + fonctionDto.getRangId()));

        if (!rang.isActif()) {
            throw new RuntimeException("Le rang spécifié n'est pas actif");
        }

        // Mettre à jour la fonction
        fonction.setNom(fonctionDto.getNom());
        fonction.setRang(rang);

        fonction = fonctionRepository.save(fonction);
        return mapToResponseDto(fonction);
    }

    // Supprimer une fonction (soft delete)
    public void deleteFonction(Long id) {
        Fonction fonction = fonctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fonction non trouvée avec l'ID: " + id));

        fonction.setActif(false);
        fonction.setDeletedAt(LocalDateTime.now());
        fonctionRepository.save(fonction);
    }

    // Recherche avec critères
    @Transactional(readOnly = true)
    public List<FonctionResponseDto> searchFonctions(String nom, Long rangId) {
        return fonctionRepository.findByCriteria(nom, rangId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Récupérer les fonctions par rang
    @Transactional(readOnly = true)
    public List<FonctionResponseDto> getFonctionsByRang(Long rangId) {
        return fonctionRepository.findByRangIdAndActifTrue(rangId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Compter les fonctions actives
    @Transactional(readOnly = true)
    public long countActiveFonctions() {
        return fonctionRepository.countByActifTrue();
    }

    // Mapper vers DTO de réponse
    private FonctionResponseDto mapToResponseDto(Fonction fonction) {
        FonctionResponseDto dto = new FonctionResponseDto();
        dto.setId(fonction.getId());
        dto.setNom(fonction.getNom());
        dto.setCreated_at(fonction.getCreated_at());
        dto.setUpdated_at(fonction.getUpdated_at());
        
        if (fonction.getRang() != null) {
            dto.setRangId(fonction.getRang().getId());
            dto.setRangNom(fonction.getRang().getNom());
            dto.setRangCode(fonction.getRang().getCode());
        }
        
        return dto;
    }
}