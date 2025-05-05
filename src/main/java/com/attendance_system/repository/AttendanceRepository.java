package com.attendance_system.repository;

import com.attendance_system.dto.AttendanceDTO;
import com.attendance_system.model.Attendance;
import com.attendance_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query("SELECT a FROM Attendance a WHERE a.date = :date")
    Page<Attendance> findAttendeesByDate(
            @Param("date") LocalDate date,
            Pageable pageable);
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);
    boolean existsByDateAndUser(LocalDate today, User user);

    @Query(value = "SELECT " +
            "AVG(EXTRACT(HOUR FROM check_in_time) * 3600 + EXTRACT(MINUTE FROM check_in_time) * 60 + EXTRACT(SECOND FROM check_in_time)) AS avg_seconds " +
            "FROM attendance " +
            "WHERE user_id = :userId " +
            "AND (CAST(:startDate AS DATE) IS NULL OR date >= :startDate) " +
            "AND (CAST(:endDate AS DATE) IS NULL OR date <= :endDate)",
            nativeQuery = true)
    Double findAverageCheckInTimeInSecondsByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT " +
            "AVG(EXTRACT(HOUR FROM check_out_time) * 3600 + EXTRACT(MINUTE FROM check_out_time) * 60 + EXTRACT(SECOND FROM check_out_time)) AS avg_seconds " +
            "FROM attendance " +
            "WHERE user_id = :userId " +
            "AND check_out_time IS NOT NULL " +
            "AND (CAST(:startDate AS DATE) IS NULL OR date >= :startDate) " +
            "AND (CAST(:endDate AS DATE) IS NULL OR date <= :endDate)",
            nativeQuery = true)
    Double findAverageCheckOutTimeInSecondsByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
    SELECT to_timestamp(AVG(EXTRACT(EPOCH FROM CAST(a.check_in_time AS TIME))))
    FROM attendance a
    JOIN nsp u ON a.user_id = u.id
    WHERE u.role = CAST(:role AS text)
    AND a.date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    Instant findAverageCheckInTimeByUserRoleAndDateRange(
            @Param("role") String role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT to_timestamp(AVG(EXTRACT(EPOCH FROM CAST(a.check_out_time AS TIME))))
    FROM attendance a
    JOIN nsp u ON a.user_id = u.id
    WHERE u.role = CAST(:role AS text)
    AND a.date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    Instant findAverageCheckOutTimeByUserRoleAndDateRange(
            @Param("role") String role,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
