package com.vjsalon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.Stylist;

@Repository
public interface StylistRepository extends JpaRepository<Stylist, Long> {
    Optional<Stylist> findByStylistCode(String stylistCode);
    List<Stylist> findByActiveTrue();
}

