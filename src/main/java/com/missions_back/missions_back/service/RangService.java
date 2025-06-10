package com.missions_back.missions_back.service;

import com.missions_back.missions_back.dto.RangDto;
import com.missions_back.missions_back.dto.RangResponseDto;
import com.missions_back.missions_back.model.Rang;
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
public class RangService {

    @Autowired
    private RangRepository rangRepository;

    // Créer un nouveau rang
    public RangResponseDto createRang(RangDto rangDto) {
        // Vérifier si le nom existe déjà
        if (rangRepository.existsByNomAndActifTrue(rangDto.getNom())) {
            throw new RuntimeException("Un rang avec ce nom existe déjà");
        }

        // Vérifier si le code existe déjà
        if (rangRepository.existsByCodeAndActifTrue(rangDto.getCode())) {
            throw new RuntimeException("Un rang avec ce code existe déjà");
        }

        // Créer le rang
        Rang rang = new Rang();
        rang.setNom(rangDto.getNom());
        rang.setCode(rangDto.getCode());
        rang.setFraisInterne(rangDto.getFraisInterne());
        rang.setFraisExterne(rangDto.getFraisExterne());

        rang = rangRepository.save(rang);
        return mapToResponseDto(rang);
    }

    // Récupérer tous les rangs actifs
    @Transactional(readOnly = true)
    public List<RangResponseDto> getAllRangs() {
        return rangRepository.findAll().stream()
                .filter(Rang::isActif)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Récupérer un rang par ID
    @Transactional(readOnly = true)
    public Optional<RangResponseDto> getRangById(Long id) {
        return rangRepository.findById(id)
                .filter(Rang::isActif)
                .map(this::mapToResponseDto);
    }

    // Mettre à jour un rang
    public RangResponseDto updateRang(Long id, RangDto rangDto) {
        Rang rang = rangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rang non trouvé avec l'ID: " + id));

        if (!rang.isActif()) {
            throw new RuntimeException("Ce rang n'est pas actif");
        }

        // Vérifier si le nouveau nom existe déjà (excluant le rang actuel)
        if (rangRepository.existsByNomAndActifTrueAndIdNot(rangDto.getNom(), id)) {
            throw new RuntimeException("Un autre rang avec ce nom existe déjà");
        }

        // Vérifier si le nouveau code existe déjà (excluant le rang actuel)
        if (rangRepository.existsByCodeAndActifTrueAndIdNot(rangDto.getCode(), id)) {
            throw new RuntimeException("Un autre rang avec ce code existe déjà");
        }

        // Mettre à jour le rang
        rang.setNom(rangDto.getNom());
        rang.setCode(rangDto.getCode());
        rang.setFraisInterne(rangDto.getFraisInterne());
        rang.setFraisExterne(rangDto.getFraisExterne());

        rang = rangRepository.save(rang);
        return mapToResponseDto(rang);
    }

    // Supprimer un rang (soft delete)
    public void deleteRang(Long id) {
        Rang rang = rangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rang non trouvé avec l'ID: " + id));
    

        rang.setActif(false);
        rang.setDeletedAt(LocalDateTime.now());
        rangRepository.save(rang);
    }

    // Recherche avec critères
    @Transactional(readOnly = true)
    public List<RangResponseDto> searchRangs(String nom, String code) {
        return rangRepository.findByCriteria(nom, code).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Compter les rangs actifs
    @Transactional(readOnly = true)
    public long countActiveRangs() {
        return rangRepository.countByActifTrue();
    }

    // Récupérer un rang par nom
    @Transactional(readOnly = true)
    public Optional<RangResponseDto> getRangByNom(String nom) {
        return rangRepository.findByNomAndActifTrue(nom)
                .map(this::mapToResponseDto);
    }

    // Récupérer un rang par code
    @Transactional(readOnly = true)
    public Optional<RangResponseDto> getRangByCode(String code) {
        return rangRepository.findByCodeAndActifTrue(code)
                .map(this::mapToResponseDto);
    }

    // Mapper vers DTO de réponse
    private RangResponseDto mapToResponseDto(Rang rang) {
        RangResponseDto dto = new RangResponseDto();
        dto.setId(rang.getId());
        dto.setNom(rang.getNom());
        dto.setCode(rang.getCode());
        dto.setFraisInterne(rang.getFraisInterne());
        dto.setFraisExterne(rang.getFraisExterne());
        dto.setCreated_at(rang.getCreated_at());
        dto.setUpdated_at(rang.getUpdated_at());
        dto.setActif(rang.isActif());
        
        
        
        return dto;
    }
}