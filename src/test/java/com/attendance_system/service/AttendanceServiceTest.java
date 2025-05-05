package com.attendance_system.service;

import com.attendance_system.dto.AttendanceDTO;
import com.attendance_system.exceptions.AttendanceServiceException;
import com.attendance_system.exceptions.InvalidSessionException;
import com.attendance_system.exceptions.ResourceNotFoundException;
import com.attendance_system.exceptions.UserNotFoundException;
import com.attendance_system.model.Attendance;
import com.attendance_system.model.Session;
import com.attendance_system.model.User;
import com.attendance_system.role.Role;
import com.attendance_system.repository.AttendanceRepository;
import com.attendance_system.repository.SessionRepository;
import com.attendance_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AttendanceServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    @Captor
    private ArgumentCaptor<LocalDateTime> startDateTimeCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> endDateTimeCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private final String SESSION_CODE = "valid-session-code";
    private final String USER_EMAIL = "test@example.com";
    private final Long USER_ID = 1L;
    private User user;
    private static final LocalTime DEFAULT_EARLY_TIME = LocalTime.of(7, 30);

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
        user = User.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
    }

    @Test
    void getFirstFiveEarlyAttendees_shouldReturnEmptyListWhenNoEarlyAttendees() {
        // Arrange
        LocalDate date = LocalDate.now();
        LocalTime lateTime = DEFAULT_EARLY_TIME.plusHours(1); // All check-ins are after early time
        Page<Attendance> emptyPage = new PageImpl<>(Collections.emptyList());

        when(attendanceRepository.findAttendeesByDate(eq(date), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        List<AttendanceDTO> result = attendanceService.getEarlyAttendees(date);

        // Assert
        assertTrue(result.isEmpty());
        verify(attendanceRepository).findAttendeesByDate(eq(date), any(Pageable.class));
    }

    @Test
    void getFirstFiveEarlyAttendees_shouldReturnEarlyAttendeesSortedAndLimited() {
        // Arrange
        LocalDate date = LocalDate.now();
        LocalDateTime earlyCheckIn = LocalDateTime.of(date, DEFAULT_EARLY_TIME.minusMinutes(30));
        LocalDateTime earlyCheckIn1 = LocalDateTime.of(date, DEFAULT_EARLY_TIME.minusMinutes(25));
        LocalDateTime lateCheckIn = LocalDateTime.of(date, DEFAULT_EARLY_TIME.plusMinutes(30));

        // Create test data - mixed early and late attendees
        List<Attendance> attendees = Arrays.asList(
                createAttendance(lateCheckIn,null),
                createAttendance( earlyCheckIn,null),
                createAttendance(lateCheckIn,null),
                createAttendance(earlyCheckIn1, null),
        createAttendance(LocalDateTime.of(date, DEFAULT_EARLY_TIME), null)
        );

        Page<Attendance> page = new PageImpl<>(attendees);

        when(attendanceRepository.findAttendeesByDate(eq(date), any(Pageable.class)))
                .thenReturn(page);

        // Act
        List<AttendanceDTO> result = attendanceService.getEarlyAttendees(date);

        // Assert
        assertEquals(2, result.size()); // Only 2 are before early time
        assertTrue(result.stream().allMatch(dto ->
                dto.getCheckInTime().toLocalTime().isBefore(DEFAULT_EARLY_TIME)));

        // Verify sorting - earliest should be first
        assertTrue(result.get(0).getCheckInTime().isBefore(result.get(1).getCheckInTime()));

        verify(attendanceRepository).findAttendeesByDate(eq(date),
                argThat(pageable ->
                        pageable.getPageNumber() == 0 &&
                                pageable.getPageSize() == 10 &&
                                pageable.getSort().equals(Sort.by("checkInTime").ascending())));
    }

    @Test
    void getFirstFiveEarlyAttendees_shouldHandleServiceException() {
        // Arrange
        LocalDate date = LocalDate.now();
        when(attendanceRepository.findAttendeesByDate(eq(date), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(AttendanceServiceException.class, () -> {
            attendanceService.getEarlyAttendees(date);
        });
    }

    private Attendance createAttendance(Long id, String firstName, String lastName, Role role, LocalDateTime checkInTime) {
        Attendance attendance = new Attendance();
        attendance.setId(id);
        attendance.setCheckInTime(checkInTime);
        attendance.setDate(checkInTime.toLocalDate());

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        attendance.setUser(user);

        return attendance;
    }

    @Test
    void checkIn_Success() {
        // Given
        when(attendanceRepository.existsByDateAndUser(any(LocalDate.class), any())).thenReturn(false);

        // When
        var result = attendanceService.checkIn(SESSION_CODE);

        // Then
        assertEquals("Attendance recorded successfully", result.getMessage());
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void checkIn_AlreadyCheckedIn() {
        // Given
        when(attendanceRepository.existsByDateAndUser(any(LocalDate.class), any())).thenReturn(true);

        // When
        var result = attendanceService.checkIn(SESSION_CODE);

        // Then
        assertEquals("Attendance already recorded for today", result.getMessage());
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
    void checkOut_InvalidSession() {
        // Given - override the default active session
        Session inactiveSession = new Session();
        inactiveSession.setSessionCode(SESSION_CODE);
        inactiveSession.setActive(false);
        when(sessionRepository.findBySessionCode(SESSION_CODE)).thenReturn(Optional.of(inactiveSession));

        // When & Then
        assertThrows(InvalidSessionException.class, () -> attendanceService.checkOut(SESSION_CODE));
        verify(attendanceRepository, never()).findByUserAndDate(any(), any());
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
                .user(user)
                .date(LocalDate.now())
                .checkInTime(checkInTime)
                .checkOutTime(checkOutTime)
                .build();
    }

}