package com.attendance_system.service;

import com.attendance_system.exceptions.AttendanceServiceException;
import com.attendance_system.exceptions.InvalidSessionException;
import com.attendance_system.exceptions.ResourceNotFoundException;
import com.attendance_system.exceptions.UserNotFoundException;
import com.attendance_system.model.Attendance;
import com.attendance_system.model.Session;
import com.attendance_system.model.User;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    private static final LocalTime EARLY_TIME = LocalTime.of(7, 30);
    private static final LocalDate START_DATE = LocalDate.of(2023, 5, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 5, 7);

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
    void getEarlyAttendees_ValidDates_ReturnsEarlyAttendees() {
        // Arrange
        Attendance earlyAttendee = createAttendance(1L, LocalDateTime.of(START_DATE, LocalTime.of(7, 0)));
        Attendance regularAttendee = createAttendance(2L, LocalDateTime.of(START_DATE, LocalTime.of(8, 0)));

        List<Attendance> allAttendees = Arrays.asList(earlyAttendee, regularAttendee);
        Page<Attendance> attendeesPage = new PageImpl<>(allAttendees, PageRequest.of(0, 10), allAttendees.size());

        when(attendanceRepository.findAttendeesBetweenDates(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(attendeesPage);

        // Act
        Page<Attendance> result = attendanceService.getEarlyAttendees(START_DATE, END_DATE, 0, 10);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(earlyAttendee.getId(), result.getContent().get(0).getId());

        verify(attendanceRepository).findAttendeesBetweenDates(
                startDateTimeCaptor.capture(), endDateTimeCaptor.capture(), pageableCaptor.capture());

        assertEquals(START_DATE.atStartOfDay(), startDateTimeCaptor.getValue());
        assertEquals(END_DATE.plusDays(1).atStartOfDay().minusNanos(1), endDateTimeCaptor.getValue());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getEarlyAttendees_NoEarlyAttendees_ReturnsEmptyPage() {
        // Arrange
        Attendance regularAttendee = createAttendance(1L, LocalDateTime.of(START_DATE, LocalTime.of(8, 0)));
        List<Attendance> allAttendees = Collections.singletonList(regularAttendee);
        Page<Attendance> attendeesPage = new PageImpl<>(allAttendees, PageRequest.of(0, 10), allAttendees.size());

        when(attendanceRepository.findAttendeesBetweenDates(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(attendeesPage);

        // Act
        Page<Attendance> result = attendanceService.getEarlyAttendees(START_DATE, END_DATE, 0, 10);

        // Assert
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getEarlyAttendees_NegativePage_UsesPageZero() {
        // Arrange
        when(attendanceRepository.findAttendeesBetweenDates(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        attendanceService.getEarlyAttendees(START_DATE, END_DATE, -1, 10);

        // Assert
        verify(attendanceRepository).findAttendeesBetweenDates(
                any(LocalDateTime.class), any(LocalDateTime.class), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getEarlyAttendees_ZeroSize_UsesDefaultSize() {
        // Arrange
        when(attendanceRepository.findAttendeesBetweenDates(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        attendanceService.getEarlyAttendees(START_DATE, END_DATE, 0, 0);

        // Assert
        verify(attendanceRepository).findAttendeesBetweenDates(
                any(LocalDateTime.class), any(LocalDateTime.class), pageableCaptor.capture());
        assertEquals(100, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getEarlyAttendees_NullStartDate_ThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                attendanceService.getEarlyAttendees(null, END_DATE, 0, 10));
        assertEquals("Start date cannot be null", exception.getMessage());

        verify(attendanceRepository, never()).findAttendeesBetweenDates(any(), any(), any());
    }

    @Test
    void getEarlyAttendees_NullEndDate_ThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                attendanceService.getEarlyAttendees(START_DATE, null, 0, 10));
        assertEquals("End date cannot be null", exception.getMessage());

        verify(attendanceRepository, never()).findAttendeesBetweenDates(any(), any(), any());
    }

    @Test
    void getEarlyAttendees_StartDateAfterEndDate_ThrowsIllegalArgumentException() {
        // Arrange
        LocalDate laterDate = LocalDate.of(2023, 5, 10);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                attendanceService.getEarlyAttendees(laterDate, START_DATE, 0, 10));
        assertEquals("Start date cannot be after end date", exception.getMessage());

        verify(attendanceRepository, never()).findAttendeesBetweenDates(any(), any(), any());
    }

    @Test
    void getEarlyAttendees_RepositoryThrowsException_ThrowsAttendanceServiceException() {
        // Arrange
        when(attendanceRepository.findAttendeesBetweenDates(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        AttendanceServiceException exception = assertThrows(AttendanceServiceException.class, () ->
                attendanceService.getEarlyAttendees(START_DATE, END_DATE, 0, 10));
        assertEquals("Failed to retrieve early attendees", exception.getMessage());
    }

    private Attendance createAttendance(Long id, LocalDateTime checkInTime) {
        Attendance attendance = new Attendance();
        attendance.setId(id);
        attendance.setCheckInTime(checkInTime);
        return attendance;
    }

    @Test
    void checkIn_Success() {
        // Given
        when(attendanceRepository.existsByDateAndUser(any(LocalDate.class), any())).thenReturn(false);

        // When
        String result = attendanceService.checkIn(SESSION_CODE);

        // Then
        assertEquals("Attendance recorded successfully", result);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void checkIn_AlreadyCheckedIn() {
        // Given
        when(attendanceRepository.existsByDateAndUser(any(LocalDate.class), any())).thenReturn(true);

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