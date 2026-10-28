package com.vjsalon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.AdminConfig;

@Repository
public interface AdminConfigRepository extends JpaRepository<AdminConfig, Long> {
    Optional<AdminConfig> findByAdminCode(String adminCode);
}

