package com.missions_back.missions_back.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.missions_back.missions_back.model.OrdreMission;

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
}