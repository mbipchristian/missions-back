package com.missions_back.missions_back.repository;

import com.missions_back.missions_back.model.Rang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RangRepository extends JpaRepository<Rang, Long> {
    
    // Recherche par nom
    Optional<Rang> findByNomAndActifTrue(String nom);
    
    // Recherche par code
    Optional<Rang> findByCodeAndActifTrue(String code);
    
    // Vérifier l'existence d'un rang avec un nom donné (pour la validation)
    boolean existsByNomAndActifTrue(String nom);
    
    // Vérifier l'existence d'un rang avec un code donné (pour la validation)
    boolean existsByCodeAndActifTrue(String code);
    
    // Vérifier l'existence d'un rang avec un nom donné, excluant un ID spécifique (pour la mise à jour)
    boolean existsByNomAndActifTrueAndIdNot(String nom, Long id);
    
    // Vérifier l'existence d'un rang avec un code donné, excluant un ID spécifique (pour la mise à jour)
    boolean existsByCodeAndActifTrueAndIdNot(String code, Long id);
    
    // Recherche avec critères
    @Query("SELECT r FROM Rang r WHERE " +
           "(:nom IS NULL OR LOWER(r.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:code IS NULL OR LOWER(r.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
           "r.actif = true")
    List<Rang> findByCriteria(@Param("nom") String nom, @Param("code") String code);
    
    // Compter les rangs actifs
    long countByActifTrue();
    
    // Trouver les rangs qui ont des fonctions associées
    @Query("SELECT DISTINCT r FROM Rang r JOIN r.fonctions f WHERE r.actif = true AND f.actif = true")
    List<Rang> findRangsWithActiveFonctions();
}