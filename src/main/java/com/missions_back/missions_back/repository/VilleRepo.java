package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.missions_back.missions_back.model.Permission;
import com.missions_back.missions_back.model.Ville;

public interface VilleRepo extends JpaRepository<Ville, Long> {
    Optional<Ville> findByName(String name);
    Optional<Ville> findByCode(String code);
    Optional<Ville> findByNameAndCode(String name, String code);
// Vérifier si une ville existe par nom ou code (pour éviter les doublons)
    @Query("SELECT COUNT(v) > 0 FROM Ville v WHERE v.name = :name OR v.code = :code")
    boolean existsByNameOrCode(@Param("name") String name, @Param("code") String code);  
    
    // Récupérer toutes les villes triées par nom
    List<Ville> findAllByOrderByNameAsc();

    // Trouver les villes par nom (recherche partielle, insensible à la casse)
    List<Ville> findByNameContainingIgnoreCase(String name);
    
    // Trouver les villes par code (recherche partielle, insensible à la casse)
    List<Ville> findByCodeContainingIgnoreCase(String code);

    Optional<Ville> findByIdAndActifTrue(Long id);
}
