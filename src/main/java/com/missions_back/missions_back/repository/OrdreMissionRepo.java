package com.missions_back.missions_back.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.missions_back.missions_back.model.OrdreMission;
import com.missions_back.missions_back.model.OrdreMissionStatut;
import com.missions_back.missions_back.model.User;

@Repository
public interface OrdreMissionRepo extends JpaRepository<OrdreMission, Long> {
    
    Optional<OrdreMission> findByReference(String reference);
    
    List<OrdreMission> findByUserId(Long userId);
    
    List<OrdreMission> findByMandatId(Long mandatId);
    
    @Query("SELECT om FROM OrdreMission om WHERE om.actif = true")
    List<OrdreMission> findAllActive();
    
    @Query("SELECT om FROM OrdreMission om WHERE om.user.id = :userId AND om.actif = true")
    List<OrdreMission> findActiveByUserId(@Param("userId") Long userId);
    
    @Query("SELECT om FROM OrdreMission om WHERE om.mandat.id = :mandatId AND om.actif = true")
    List<OrdreMission> findActiveByMandatId(@Param("mandatId") Long mandatId);
    
    boolean existsByReference(String reference);
    
    @Query("SELECT COUNT(om) FROM OrdreMission om WHERE om.mandat.id = :mandatId AND om.actif = true")
    Long countActiveByMandatId(@Param("mandatId") Long mandatId);

    @Query("SELECT om FROM OrdreMission om WHERE om.user.id = :userId AND om.dateFin >= :dateDebut AND om.actif = true")
    List<OrdreMission> findActiveByUserIdWithDateConflict(@Param("userId") Long userId, @Param("dateDebut") Date dateDebut);
    
    @Query("SELECT SUM(om.duree) FROM OrdreMission om WHERE om.user.id = :userId AND om.actif = true")
    Long sumDureeByUserId(@Param("userId") Long userId);
    
    // Nouvelles méthodes utilisées dans OrdreMissionService
    List<OrdreMission> findByStatut(OrdreMissionStatut statut);
    
    List<OrdreMission> findByStatutIn(List<OrdreMissionStatut> statuts);
    
    // In your repository interface
List<OrdreMission> findByUserIdAndStatutNot(Long userId, OrdreMissionStatut statut);
    
    @Query("SELECT om FROM OrdreMission om WHERE om.statut = :statut AND om.dateDebut <= :date AND om.actif = true")
    List<OrdreMission> findByStatutAndDateDebutLessThanEqual(@Param("statut") OrdreMissionStatut statut, @Param("date") Date date);
    
    @Query("SELECT om FROM OrdreMission om WHERE om.statut = :statut AND om.dateFin <= :date AND om.actif = true")
    List<OrdreMission> findByStatutAndDateFinLessThanEqual(@Param("statut") OrdreMissionStatut statut, @Param("date") Date date);

    /**
     * Trouve l'ordre de mission le plus récent d'un utilisateur (par date de fin)
     * qui est encore actif
     */
    Optional<OrdreMission> findTopByUserAndActifTrueOrderByDateFinDesc(User user);
    
    /**
     * Alternative avec une requête personnalisée si nécessaire
     */
    @Query("SELECT om FROM OrdreMission om WHERE om.user = :user AND om.actif = true ORDER BY om.dateFin DESC")
    Optional<OrdreMission> findLatestActiveOrdreMissionByUser(@Param("user") User user);
}