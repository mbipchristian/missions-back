package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.missions_back.missions_back.model.Ressource;

public interface RessourceRepo extends JpaRepository<Ressource, Long> {
    Optional<Ressource> findByName(String name);
    
    List<Ressource> findByNameContainingIgnoreCase(String name);
    
    boolean existsByName(String name);
    
    Optional<Ressource> findByIdAndActifTrue(Long id);
    
}
