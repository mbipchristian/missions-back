package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.missions_back.missions_back.model.Rapport;

@Repository
public interface RapportRepo extends JpaRepository<Rapport, Long> {
    
    Optional<Rapport> findByReference(String reference);
    
    List<Rapport> findByUserId(Long userId);
    
    Optional<Rapport> findByMandatId(Long mandatId);
    
    @Query("SELECT r FROM Rapport r WHERE r.actif = true")
    List<Rapport> findAllActive();
    
    @Query("SELECT r FROM Rapport r WHERE r.user.id = :userId AND r.actif = true")
    List<Rapport> findActiveByUserId(@Param("userId") Long userId);
    
    boolean existsByReference(String reference);
    
    boolean existsByMandatId(Long mandatId);
}