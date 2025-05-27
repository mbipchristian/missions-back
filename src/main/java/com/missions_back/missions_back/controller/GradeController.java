package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.GradeDto;
import com.missions_back.missions_back.dto.GradeResponseDto;
import com.missions_back.missions_back.model.Grade;
import com.missions_back.missions_back.service.GradeService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("auth/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    //Créer un grade
    @PostMapping("/create")
    public ResponseEntity<?> createGrade(@RequestBody GradeDto gradeDto) {
        try {
            GradeResponseDto createdGrade = gradeService.createGrade(gradeDto);
            return ResponseEntity.ok(createdGrade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating grade: " + e.getMessage());
        }
    }

    //Récupérer tous les grades
    @GetMapping("/all")
    public ResponseEntity<List<Grade>> getAllGrades() {
        List<Grade> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(grades);
    }
    //Récupérer un grade par son id
    @GetMapping("/{id}")
    public ResponseEntity<GradeResponseDto> getGradeById(@PathVariable Long id) {
        try {
            GradeResponseDto grade = gradeService.getGradeById(id);
            return new ResponseEntity<>(grade, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //Mettre à jour un grade existant
    @PutMapping("/{id}")
    public ResponseEntity<GradeResponseDto> updateGrade(
            @PathVariable Long id, 
            @RequestBody GradeDto gradeDto) {
        try {
            GradeResponseDto updatedGrade = gradeService.updateGrade(id, gradeDto);
            return new ResponseEntity<>(updatedGrade, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    //Supprimer un grade
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        try {
            gradeService.deleteGrade(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
