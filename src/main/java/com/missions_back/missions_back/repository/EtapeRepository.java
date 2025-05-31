package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.missions_back.missions_back.model.Etape;

@Repository
public interface EtapeRepository extends JpaRepository<Etape, Long> {
    
    // Trouver toutes les étapes d'un mandat
    @Query("SELECT e FROM Etape e WHERE e.mandat.id = :mandatId ORDER BY e.ordre")
    List<Etape> findByMandatIdOrderByOrdre(@Param("mandatId") Long mandatId);
    
    // Trouver une étape par ID avec toutes ses relations
    @Query("SELECT e FROM Etape e LEFT JOIN FETCH e.users LEFT JOIN FETCH e.villes LEFT JOIN FETCH e.ressources WHERE e.id = :id")
    Optional<Etape> findByIdWithRelations(@Param("id") Long id);
    
    // Trouver toutes les étapes actives d'un mandat
    @Query("SELECT e FROM Etape e WHERE e.mandat.id = :mandatId AND e.actif = true ORDER BY e.ordre")
    List<Etape> findActiveByMandatId(@Param("mandatId") Long mandatId);
    
    // Vérifier si une étape existe avec un certain ordre dans un mandat
    @Query("SELECT COUNT(e) > 0 FROM Etape e WHERE e.mandat.id = :mandatId AND e.ordre = :ordre AND e.actif = true")
    boolean existsByMandatIdAndOrdre(@Param("mandatId") Long mandatId, @Param("ordre") int ordre);
    
    // Trouver la prochaine étape dans l'ordre
    @Query("SELECT e FROM Etape e WHERE e.mandat.id = :mandatId AND e.ordre > :currentOrdre AND e.actif = true ORDER BY e.ordre LIMIT 1")
    Optional<Etape> findNextEtape(@Param("mandatId") Long mandatId, @Param("currentOrdre") int currentOrdre);
    
    // Trouver l'étape précédente dans l'ordre
    @Query("SELECT e FROM Etape e WHERE e.mandat.id = :mandatId AND e.ordre < :currentOrdre AND e.actif = true ORDER BY e.ordre DESC LIMIT 1")
    Optional<Etape> findPreviousEtape(@Param("mandatId") Long mandatId, @Param("currentOrdre") int currentOrdre);
}