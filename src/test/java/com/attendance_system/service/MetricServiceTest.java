package com.attendance_system.service;

import com.attendance_system.model.User;
import com.attendance_system.repository.AttendanceRepository;
import com.attendance_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MetricsService metricsService;

    private final LocalDate START_DATE = LocalDate.of(2023, 1, 1);
    private final LocalDate END_DATE = LocalDate.of(2023, 12, 31);
    private final String USER_EMAIL = "test@example.com";
    private final Long USER_ID = 1L;
    private final User testUser = User.builder().id(USER_ID).email(USER_EMAIL).build();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAverageCheckInTime_ShouldReturnLocalTime() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(java.util.Optional.of(testUser));
        when(attendanceRepository.findAverageCheckInTimeInSecondsByUserId(USER_ID)).thenReturn(36000.0); // 10:00:00

        var result = metricsService.getAverageCheckInTime();

        assertEquals(LocalTime.of(10, 0), result.getData());
        verify(userRepository).findByEmail(USER_EMAIL);
        verify(attendanceRepository).findAverageCheckInTimeInSecondsByUserId(USER_ID);
    }

    @Test
    void getAverageCheckInTime_ShouldThrowWhenUserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> metricsService.getAverageCheckInTime());
    }

    @Test
    void getAverageCheckOutTime_ShouldReturnLocalTime() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(java.util.Optional.of(testUser));
        when(attendanceRepository.findAverageCheckOutTimeInSecondsByUserId(USER_ID)).thenReturn(64800.0); // 18:00:00

        var result = metricsService.getAverageCheckOutTime();

        assertEquals(LocalTime.of(18, 0), result.getData());
    }

    @Test
    void getAverageCheckInTimeForNSPS_ShouldReturnLocalTime() {
        Instant testInstant = LocalDateTime.of(2023, 1, 1, 8, 30).toInstant(ZoneOffset.UTC);
        when(attendanceRepository.findAverageCheckInTimeByUserRoleAndDateRange("NSP", START_DATE, END_DATE))
                .thenReturn(testInstant);

        LocalTime result = metricsService.getAverageCheckInTimeForNSPS(START_DATE, END_DATE);

        assertEquals(LocalTime.of(8, 30), result);
    }

    @Test
    void getAverageCheckInTimeForNSPS_ShouldReturnNullWhenNoData() {
        when(attendanceRepository.findAverageCheckInTimeByUserRoleAndDateRange("NSP", START_DATE, END_DATE))
                .thenReturn(null);

        LocalTime result = metricsService.getAverageCheckInTimeForNSPS(START_DATE, END_DATE);

        assertNull(result);
    }

    @Test
    void getAverageCheckInTimeForNSPS_ShouldThrowWhenDatesAreNull() {
        assertThrows(IllegalArgumentException.class, () -> metricsService.getAverageCheckInTimeForNSPS(null, END_DATE));
        assertThrows(IllegalArgumentException.class, () -> metricsService.getAverageCheckInTimeForNSPS(START_DATE, null));
        assertThrows(IllegalArgumentException.class, () -> metricsService.getAverageCheckInTimeForNSPS(null, null));
    }

    @Test
    void getAverageCheckInTimeForNSPS_ShouldThrowWhenStartDateAfterEndDate() {
        LocalDate invalidStartDate = END_DATE.plusDays(1);
        assertThrows(IllegalArgumentException.class,
                () -> metricsService.getAverageCheckInTimeForNSPS(invalidStartDate, END_DATE));
    }

    @Test
    void getAverageCheckOutTimeForFacilitators_ShouldReturnLocalTime() {
        Instant testInstant = LocalDateTime.of(2023, 1, 1, 17, 45).toInstant(ZoneOffset.UTC);
        when(attendanceRepository.findAverageCheckOutTimeByUserRoleAndDateRange("FACILITATOR", START_DATE, END_DATE))
                .thenReturn(testInstant);

        LocalTime result = metricsService.getAverageCheckOutTimeForFacilitators(START_DATE, END_DATE);

        assertEquals(LocalTime.of(17, 45), result);
    }


}