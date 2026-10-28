package com.vjsalon.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.DailyLog;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    List<DailyLog> findByLogDateOrderByCreatedAtDesc(LocalDate logDate);

    List<DailyLog> findByStylistIdAndLogDate(Long stylistId, LocalDate logDate);

    List<DailyLog> findByLogDateBetween(LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT SUM(amount) FROM daily_log WHERE log_date = :date", nativeQuery = true)
    Double getDailyRevenue(@Param("date") LocalDate date);

    @Query(value = "SELECT SUM(amount) FROM daily_log WHERE log_date BETWEEN :start AND :end", nativeQuery = true)
    Double getRevenueBetween(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}