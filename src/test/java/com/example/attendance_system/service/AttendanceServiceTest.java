package com.example.attendance_system.service;
import com.example.attendance_system.exceptions.AttendanceServiceException;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

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

    private static final LocalTime EARLY_TIME = LocalTime.of(7, 30);
    private static final LocalDate START_DATE = LocalDate.of(2023, 5, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 5, 7);

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
}