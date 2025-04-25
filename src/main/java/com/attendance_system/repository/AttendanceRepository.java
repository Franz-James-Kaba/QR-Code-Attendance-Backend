package com.attendance_system.repository;

import com.attendance_system.model.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query("SELECT a FROM Attendance a WHERE a.checkInTime >= :startDateTime AND a.checkInTime <= :endDateTime")
    Page<Attendance> findAttendeesBetweenDates(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);
    Optional<Attendance> findByUserIdAndDate(Long userId, LocalDate date);
    boolean existsByDate(LocalDate today);
}
