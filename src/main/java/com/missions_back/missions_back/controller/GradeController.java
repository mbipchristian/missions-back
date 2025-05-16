package com.missions_back.missions_back.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.missions_back.missions_back.dto.GradeDto;
import com.missions_back.missions_back.model.Grade;
import com.missions_back.missions_back.service.GradeService;

@RestController
@RequestMapping("/auth/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    //Créer un grade
    @PostMapping("/create")
    public ResponseEntity<?> createGrade(@RequestBody GradeDto gradeDto) {
        try {
            Grade createdGrade = gradeService.createGrade(gradeDto);
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
}
