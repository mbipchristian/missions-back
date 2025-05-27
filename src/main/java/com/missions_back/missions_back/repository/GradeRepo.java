package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.missions_back.missions_back.model.Grade;

public interface GradeRepo extends JpaRepository<Grade, Long> {
    Optional<Grade> findByName(String name);
    List<Grade> findByActifTrue();
    Optional<Grade> findByIdAndActifTrue(Long id);
}
