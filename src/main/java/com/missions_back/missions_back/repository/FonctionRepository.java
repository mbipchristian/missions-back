package com.missions_back.missions_back.repository;

import com.missions_back.missions_back.model.Fonction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FonctionRepository extends JpaRepository<Fonction, Long> {
    
    // Recherche par nom
    Optional<Fonction> findByNomAndActifTrue(String nom);
    
    // Recherche par rang
    List<Fonction> findByRangIdAndActifTrue(Long rangId);
    
    // Vérifier l'existence d'une fonction avec un nom donné (pour la validation)
    boolean existsByNomAndActifTrue(String nom);
    
    // Vérifier l'existence d'une fonction avec un nom donné, excluant un ID spécifique (pour la mise à jour)
    boolean existsByNomAndActifTrueAndIdNot(String nom, Long id);
    
    // Recherche avec critères
    @Query("SELECT f FROM Fonction f WHERE " +
           "(:nom IS NULL OR LOWER(f.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:rangId IS NULL OR f.rang.id = :rangId) AND " +
           "f.actif = true")
    List<Fonction> findByCriteria(@Param("nom") String nom, @Param("rangId") Long rangId);
    
    // Compter les fonctions actives
    long countByActifTrue();
    
    // Compter les fonctions par rang
    long countByRangIdAndActifTrue(Long rangId);
}