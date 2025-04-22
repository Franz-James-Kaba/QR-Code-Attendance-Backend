package com.example.attendance_system.service;

import com.example.attendance_system.exceptions.InvalidSessionException;
import com.example.attendance_system.exceptions.ResourceNotFoundException;
import com.example.attendance_system.exceptions.UserNotFoundException;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.Session;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repository.AttendanceRepository;
import com.example.attendance_system.repository.SessionRepository;
import com.example.attendance_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AttendanceServiceTest {
    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AttendanceService attendanceService;

    private final String SESSION_CODE = "valid-session-code";
    private final String USER_EMAIL = "test@example.com";
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // Setup SecurityContextHolder mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        SecurityContextHolder.setContext(securityContext);

        // Setup Session mock - active session is the default case
        Session activeSession = Session.builder()
                .sessionCode(SESSION_CODE)
                .active(true)
                .build();
        when(sessionRepository.findBySessionCode(SESSION_CODE)).thenReturn(Optional.of(activeSession));

        // Setup User mock
        User user = User.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
    }

    @Test
    void checkIn_Success() {
        // Given
        when(attendanceRepository.existsByDate(any(LocalDate.class))).thenReturn(false);

        // When
        String result = attendanceService.checkIn(SESSION_CODE);

        // Then
        assertEquals("Attendance recorded successfully", result);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void checkIn_AlreadyCheckedIn() {
        // Given
        when(attendanceRepository.existsByDate(any(LocalDate.class))).thenReturn(true);

        // When
        String result = attendanceService.checkIn(SESSION_CODE);

        // Then
        assertEquals("Attendance already recorded for today", result);
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void checkIn_InvalidSession() {
        // Given - override the default active session
        Session inactiveSession = new Session();
        inactiveSession.setSessionCode(SESSION_CODE);
        inactiveSession.setActive(false);
        when(sessionRepository.findBySessionCode(SESSION_CODE)).thenReturn(Optional.of(inactiveSession));

        // When & Then
        assertThrows(InvalidSessionException.class, () -> attendanceService.checkIn(SESSION_CODE));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void checkIn_SessionNotFound() {
        // Given - override the default session behavior
        when(sessionRepository.findBySessionCode(SESSION_CODE)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> attendanceService.checkIn(SESSION_CODE));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void checkIn_UserNotFound() {
        // Given - override the default user behavior
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> attendanceService.checkIn(SESSION_CODE));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void checkOut_Success() {
        // Given
        LocalDateTime checkInTime = LocalDateTime.now().minusHours(9);
        Attendance attendance = createAttendance(checkInTime, null);

        when(attendanceRepository.findByUserIdAndDate(eq(USER_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(attendance));

        // When
        String result = attendanceService.checkOut(SESSION_CODE);

        // Then
        assertEquals("Checked out successfully", result);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void checkOut_BeforeMinimumWorkPeriod() {
        // Given
        LocalDateTime checkInTime = LocalDateTime.now().minusHours(7); // 7 hours ago, less than required 9
        Attendance attendance = createAttendance(checkInTime, null);

        when(attendanceRepository.findByUserIdAndDate(eq(USER_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(attendance));

        // When
        String result = attendanceService.checkOut(SESSION_CODE);

        // Then
        assertEquals("Cannot check out before minimum work period (8 hours)", result);
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void checkOut_NoCheckInRecord() {
        // Given
        when(attendanceRepository.findByUserIdAndDate(eq(USER_ID), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> attendanceService.checkOut(SESSION_CODE));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void checkOut_InvalidSession() {
        // Given - override the default active session
        Session inactiveSession = new Session();
        inactiveSession.setSessionCode(SESSION_CODE);
        inactiveSession.setActive(false);
        when(sessionRepository.findBySessionCode(SESSION_CODE)).thenReturn(Optional.of(inactiveSession));

        // When & Then
        assertThrows(InvalidSessionException.class, () -> attendanceService.checkOut(SESSION_CODE));
        verify(attendanceRepository, never()).findByUserIdAndDate(any(), any());
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void checkOut_SessionNotFound() {
        // Given - override the default session behavior
        when(sessionRepository.findBySessionCode(SESSION_CODE)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> attendanceService.checkOut(SESSION_CODE));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void checkOut_UserNotFound() {
        // Given - override the default user behavior
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> attendanceService.checkOut(SESSION_CODE));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    // Helper methods
    private Attendance createAttendance(LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        return Attendance.builder()
                .userId(USER_ID)
                .date(LocalDate.now())
                .checkInTime(checkInTime)
                .checkOutTime(checkOutTime)
                .build();
    }
}
