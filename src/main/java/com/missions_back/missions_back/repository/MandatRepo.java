package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.missions_back.missions_back.model.Mandat;

public interface MandatRepo extends JpaRepository<Mandat, Long> {
    Optional<Mandat> findByReference(String reference);
    
    boolean existsByReference(String reference);
    
    Optional<Mandat> findByMissionDeControle(boolean missionDeControle);
        
    List<Mandat> findByUsersId(Long userId);
    
    Optional<Mandat> findByVillesId(Long villeId);

    List<Mandat> findByActifTrue();
    
}
