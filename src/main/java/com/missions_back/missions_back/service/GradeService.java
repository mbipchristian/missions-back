package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.GradeDto;
import com.missions_back.missions_back.dto.GradeResponseDto;
import com.missions_back.missions_back.model.Grade;
import com.missions_back.missions_back.repository.GradeRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GradeService {
    private final GradeRepo gradeRepo;
    
    public GradeService(GradeRepo gradeRepo) {
        this.gradeRepo = gradeRepo;
    }

    //Creation d'un nouveau grade
    @Transactional
    public GradeResponseDto createGrade(GradeDto gradeDto) {

        // Vérifier si le grade existe déjà
        if (gradeRepo.findByName(gradeDto.name()).isPresent()) {
            throw new IllegalArgumentException("Grade already exists");
        }

        // Créer un nouveau grade
        Grade grade = new Grade();
        grade.setName(gradeDto.name());
        grade.setFraisExterne(gradeDto.fraisExterne());
        grade.setFraisInterne(gradeDto.fraisInterne());
        grade.setCode(gradeDto.code());
        grade.setActif(true);
        grade.setCreated_at(LocalDateTime.now());
        grade.setUpdated_at(LocalDateTime.now());

        // Enregistrer le grade dans la base de données
        Grade createdGrade = gradeRepo.save(grade);
        return convertToGradeResponseDto(createdGrade);
    }

    //Récupérer tous les grades actifs
    public List<Grade> getAllGrades() {
        return gradeRepo.findByActifTrue();
    }
    //Récupère un grade par son ID
    public GradeResponseDto getGradeById(Long id) {
        Grade grade = gradeRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        return convertToGradeResponseDto(grade);
    }
    
    
     //Met à jour un grade existante
    @Transactional
    public GradeResponseDto updateGrade(Long id, GradeDto gradeDto) {
        Grade grade = gradeRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        
        //Vérifier si le nouveau nom existe déjà pour un autre grade
        Optional<Grade> existingGradeWithName = gradeRepo.findByName(gradeDto.name());
        if (existingGradeWithName.isPresent() && !existingGradeWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Un autre rôle avec ce nom existe déjà");
        }
        
        grade.setName(gradeDto.name());
        
        Grade updatedGrade = gradeRepo.save(grade);
        return convertToGradeResponseDto(updatedGrade);
    }
    
    // Supprime logiquement un grade
    @Transactional
    public void deleteGrade(Long id) {
        Grade grade = gradeRepo.findByIdAndActifTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID : " + id));
        grade.setActif(false);
        grade.setDeleted_at(LocalDateTime.now());
        gradeRepo.save(grade);
    }

    //Methode de conversion de Grade vers GradeResponseDto
    private GradeResponseDto convertToGradeResponseDto(Grade grade) {
        return new GradeResponseDto(
            grade.getId(),
            grade.getName(),
            grade.getCode(),
            grade.getFraisExterne(),
            grade.getFraisInterne(),
            grade.getCreated_at(),
            grade.getUpdated_at()
        );
    }

}
