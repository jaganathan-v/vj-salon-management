package com.vjsalon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.SalonService;

@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {

    List<SalonService> findByActiveTrueOrderByDisplayOrderAsc();
}