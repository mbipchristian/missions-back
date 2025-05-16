package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.missions_back.missions_back.model.Permission;

public interface PermissionRepo extends JpaRepository <Permission, Long> {
    Optional<Permission> findByName(String name);

    List<Permission> findByActifTrue();
    
    Optional<Permission> findByIdAndActifTrue(Long id);
}
