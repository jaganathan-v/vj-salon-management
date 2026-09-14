package com.vjsalon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.ShopStatus;

@Repository
public interface ShopStatusRepository extends JpaRepository<ShopStatus, Long> {}

