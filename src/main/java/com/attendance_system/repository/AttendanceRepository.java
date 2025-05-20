package com.attendance_system.repository;

import com.attendance_system.model.Attendance;
import com.attendance_system.model.Session;
import com.attendance_system.model.User;
import com.attendance_system.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
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
    SELECT to_timestamp(AVG(EXTRACT(EPOCH FROM check_in_time)))
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
    SELECT to_timestamp(AVG(EXTRACT(EPOCH FROM check_out_time)))
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

    List<Attendance> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);

    int countByDateAndUserRole(LocalDate date, Role role);

//    List<Attendance> findByDateAndUserRoleOrderByPosition(LocalDate date, Role role);
//    List<Attendance> findByDateOrderByUserRoleAscPositionAsc(LocalDate date);

    @Query("SELECT SUM(a.point) FROM Attendance a WHERE a.user.id = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);

    @Query(value =
            "SELECT u.id as user_id, u.first_name as first_name, u.last_name as last_name, " +
                    "COALESCE(SUM(a.point), 0) as total_points, " +
                    "RANK() OVER (ORDER BY COALESCE(SUM(a.point), 0) DESC) as position " +
                    "FROM nsp u " +
                    "LEFT JOIN attendance a ON u.id = a.user_id " +
                    "WHERE u.role = 'NSP' " +  // Filter only users with NSP role
                    "GROUP BY u.id, u.first_name, u.last_name " +
                    "ORDER BY total_points DESC",
            nativeQuery = true)
    List<Map<String, Object>> findAllNspUsersWithTotalPointsAndRank();


    @Query(value = """
    SELECT COUNT(DISTINCT a.date)
    FROM attendance a
    WHERE a.user_id = :userId
      AND EXTRACT(MONTH FROM a.date) = :month
      AND EXTRACT(YEAR FROM a.date) = :year
      AND EXTRACT(DOW FROM a.date) BETWEEN 1 AND 5
    """, nativeQuery = true)
    int countWeekdayAttendancesByUserAndMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    List<Attendance> findBySession(Session session);

    boolean existsByUserIdAndDateAndCheckInTimeIsNotNullAndCheckOutTimeIsNull(Long id, LocalDate date);
}