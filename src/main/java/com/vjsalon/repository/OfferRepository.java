package com.vjsalon.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByActiveTrueAndValidUntilGreaterThanEqual(LocalDate date);
    List<Offer> findAllByOrderByValidUntilDesc();
}

