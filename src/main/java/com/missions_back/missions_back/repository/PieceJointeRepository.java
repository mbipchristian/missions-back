package com.missions_back.missions_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.missions_back.missions_back.model.PieceJointe;

@Repository
public interface PieceJointeRepository extends JpaRepository<PieceJointe, Long> {
    
    // Rechercher par mandat
    List<PieceJointe> findByMandatIdAndActifTrue(Long mandatId);
    
    // Rechercher par ordre de mission
    List<PieceJointe> findByOrdreMissionIdAndActifTrue(Long ordreMissionId);
    
    // Rechercher par rapport
    List<PieceJointe> findByRapportIdAndActifTrue(Long rapportId);
    
    // Rechercher par utilisateur
    List<PieceJointe> findByUserIdAndActifTrue(Long userId);
    
    // Rechercher par type MIME
    List<PieceJointe> findByTypeMimeAndActifTrue(String typeMime);
    
    // Rechercher par nom (contient)
    @Query("SELECT p FROM PieceJointe p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%')) AND p.actif = true")
    List<PieceJointe> findByNomContainingIgnoreCaseAndActifTrue(@Param("nom") String nom);
    
    // Rechercher toutes les pièces jointes actives
    List<PieceJointe> findByActifTrue();
    
    // Vérifier l'existence d'une pièce jointe par chemin
    boolean existsByCheminFichierAndActifTrue(String cheminFichier);
    
    // Compter les pièces jointes par mandat
    @Query("SELECT COUNT(p) FROM PieceJointe p WHERE p.mandat.id = :mandatId AND p.actif = true")
    long countByMandatIdAndActifTrue(@Param("mandatId") Long mandatId);
    
    // Compter les pièces jointes par ordre de mission
    @Query("SELECT COUNT(p) FROM PieceJointe p WHERE p.ordreMission.id = :ordreMissionId AND p.actif = true")
    long countByOrdreMissionIdAndActifTrue(@Param("ordreMissionId") Long ordreMissionId);
    
    // Compter les pièces jointes par rapport
    @Query("SELECT COUNT(p) FROM PieceJointe p WHERE p.rapport.id = :rapportId AND p.actif = true")
    long countByRapportIdAndActifTrue(@Param("rapportId") Long rapportId);
}