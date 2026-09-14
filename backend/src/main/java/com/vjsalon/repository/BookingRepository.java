package com.vjsalon.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByOrderByCreatedAtDesc();
    List<Booking> findByStatusOrderByCreatedAtDesc(String status);
    List<Booking> findByBookingDateOrderByBookingTimeAsc(LocalDate bookingDate);
    List<Booking> findByStylistIdAndBookingDate(Long stylistId, LocalDate bookingDate);
}

