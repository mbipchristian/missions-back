package com.missions_back.missions_back.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.MandatStatut;

@Repository
public interface MandatRepo extends JpaRepository<Mandat, Long> {
    
    Optional<Mandat> findByReference(String reference);
    
    boolean existsByReference(String reference);
    
    Optional<Mandat> findByMissionDeControle(boolean missionDeControle);
        
    List<Mandat> findByUsersId(Long userId);
    
    Optional<Mandat> findByVillesId(Long villeId);

    List<Mandat> findByActifTrue();
    
    // Nouvelles méthodes utilisées dans MandatService
    List<Mandat> findByStatut(MandatStatut statut);
    
    @Query("SELECT m FROM Mandat m JOIN m.users u WHERE u.id = :userId AND m.statut != :statut AND m.actif = true")
    List<Mandat> findByUsersIdAndStatutNot(@Param("userId") Long userId, @Param("statut") MandatStatut statut);
    
    @Query("SELECT m FROM Mandat m WHERE m.statut = :statut AND m.dateDebut <= :date AND m.actif = true")
    List<Mandat> findByStatutAndDateDebutLessThanEqual(@Param("statut") MandatStatut statut, @Param("date") Date date);
    
    @Query("SELECT m FROM Mandat m WHERE m.statut = :statut AND m.dateFin <= :date AND m.actif = true")
    List<Mandat> findByStatutAndDateFinLessThanEqual(@Param("statut") MandatStatut statut, @Param("date") Date date);

    List<Mandat> findByStatutAndActifTrue(MandatStatut statut);

    @Query("SELECT m FROM Mandat m JOIN m.users u WHERE u.id = :userId AND m.statut != :statut AND m.actif = true")
    List<Mandat> findByUsersIdAndStatutNotAndActifTrue(@Param("userId") Long userId, @Param("statut") MandatStatut statut);
}