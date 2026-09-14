package com.vjsalon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.ShopSettings;

@Repository
public interface ShopSettingsRepository extends JpaRepository<ShopSettings, Long> {}

