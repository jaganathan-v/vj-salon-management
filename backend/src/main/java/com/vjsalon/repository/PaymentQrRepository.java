package com.vjsalon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.PaymentQr;

@Repository
public interface PaymentQrRepository extends JpaRepository<PaymentQr, Long> {}

