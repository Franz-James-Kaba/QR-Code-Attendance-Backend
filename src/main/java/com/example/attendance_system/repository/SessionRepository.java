package com.example.attendance_system.repository;

import com.example.attendance_system.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session> findBySessionCode(String sessionCode);

    @Modifying
    @Query("UPDATE Session s SET s.active = :status " +
           "WHERE s.active = true AND s.endTime < :currentTime")
    void updateStatusForExpiredSessions(LocalDateTime currentTime, boolean status);

    @Modifying
    @Query("UPDATE Session s SET s.active = :status " +
           "WHERE s.active = false " +
           "AND s.startTime <= :currentTime " +
           "AND s.endTime > :currentTime")
    void updateStatusForEligibleSessions(LocalDateTime currentTime, boolean status);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.endTime < :currentTime")
    void deleteExpiredSessions(LocalDateTime currentTime);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.endTime < :currentTime")
    long countExpiredSessions(LocalDateTime currentTime);


}

