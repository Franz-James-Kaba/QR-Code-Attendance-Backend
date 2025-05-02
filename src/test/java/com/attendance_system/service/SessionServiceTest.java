package com.attendance_system.service;

import com.attendance_system.model.Session;
import com.attendance_system.repository.SessionRepository;
import com.attendance_system.request.GenerateSessionRequest;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private QRCodeGenerator qrCodeGenerator;

    @InjectMocks
    private SessionService sessionService;

    private GenerateSessionRequest request;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now().minusMinutes(1); // already started
        endTime = LocalDateTime.now().plusHours(1);
        request = mock(GenerateSessionRequest.class);
        when(request.startTime()).thenReturn(startTime);
        when(request.endTime()).thenReturn(endTime);
    }

    @Test
    void testGenerateQRCode_Success() throws IOException, WriterException {
        int width = 300;
        int height = 300;
        ByteArrayOutputStream qrStream = new ByteArrayOutputStream();
        qrStream.write("test-qr".getBytes());

        when(qrCodeGenerator.generateQRCode(anyString(), eq(width), eq(height)))
                .thenReturn(qrStream);

        // Act
        var result = sessionService.generateQRCode(request, width, height);

        // Assert
        assertNotNull(result);
        assertArrayEquals(qrStream.toByteArray(), result.getQrCodeImage());

        // Verify session is saved with correct properties
        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        Session savedSession = sessionCaptor.getValue();

        assertNotNull(savedSession.getSessionCode());
        assertEquals(startTime, savedSession.getStartTime());
        assertEquals(endTime, savedSession.getEndTime());
        assertTrue(savedSession.isActive());
    }

    @Test
    void testGenerateQRCode_SessionNotActive() throws IOException, WriterException {
        // startTime is in the future
        LocalDateTime futureStart = LocalDateTime.now().plusMinutes(10);
        LocalDateTime futureEnd = LocalDateTime.now().plusHours(2);
        when(request.startTime()).thenReturn(futureStart);
        when(request.endTime()).thenReturn(futureEnd);

        int width = 200;
        int height = 200;
        ByteArrayOutputStream qrStream = new ByteArrayOutputStream();
        qrStream.write("future-qr".getBytes());

        when(qrCodeGenerator.generateQRCode(anyString(), eq(width), eq(height)))
                .thenReturn(qrStream);

        // Act
        var result = sessionService.generateQRCode(request, width, height);

        // Assert
        assertNotNull(result);
        assertArrayEquals(qrStream.toByteArray(), result.getQrCodeImage());

        // Verify session is saved with correct properties
        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        Session savedSession = sessionCaptor.getValue();

        assertNotNull(savedSession.getSessionCode());
        assertEquals(futureStart, savedSession.getStartTime());
        assertEquals(futureEnd, savedSession.getEndTime());
        assertFalse(savedSession.isActive());
    }
}
