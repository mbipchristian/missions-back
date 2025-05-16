package com.missions_back.missions_back.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.GradeDto;
import com.missions_back.missions_back.model.Grade;
import com.missions_back.missions_back.repository.GradeRepo;

@Service
public class GradeService {
    private final GradeRepo gradeRepo;
    
    public GradeService(GradeRepo gradeRepo) {
        this.gradeRepo = gradeRepo;
    }

    //Creation d'un nouveau grade
    @Transactional
    public Grade createGrade(GradeDto gradeDto) {

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

        // Enregistrer le grade dans la base de données
        return gradeRepo.save(grade);
    }

    //Récupérer tous les grades actifs
    public List<Grade> getAllGrades() {
        return gradeRepo.findByActifTrue();
    }

}
