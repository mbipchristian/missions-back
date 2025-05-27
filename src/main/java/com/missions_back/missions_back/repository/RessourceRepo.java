package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.missions_back.missions_back.model.Ressource;

public interface RessourceRepo extends JpaRepository<Ressource, Long> {
    Optional<Ressource> findByCode(String code);
    
    List<Ressource> findByNameContainingIgnoreCase(String name);
    
    // @Query("SELECT r FROM Ressource r WHERE r.quantite < :seuil")
    // List<Ressource> findRessourcesWithLowStock(@Param("seuil") Long seuil);
    
    boolean existsByCode(String code);
    
    Optional<Ressource> findByIdAndActifTrue(Long id);
    
}
