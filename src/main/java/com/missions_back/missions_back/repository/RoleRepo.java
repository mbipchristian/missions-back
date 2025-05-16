package com.missions_back.missions_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.missions_back.missions_back.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    List<Role> findByActifTrue();
    Optional<Role> findByIdAndActifTrue(Long id);
}
