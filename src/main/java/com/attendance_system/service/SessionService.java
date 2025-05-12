package com.attendance_system.service;

import com.attendance_system.exceptions.ResourceNotFoundException;
import com.attendance_system.model.Session;
import com.attendance_system.repository.SessionRepository;
import com.attendance_system.request.GenerateSessionRequest;
import com.attendance_system.request.UpdateSessionRequest;
import com.attendance_system.response.SuccessResponse;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository repository;
    private final QRCodeGenerator qrCodeGenerator;
    private final SessionSchedulerService sessionSchedulerService;

    public byte[] generateQRCode(GenerateSessionRequest request, int width, int height) throws IOException, WriterException {
        var sessionCode = UUID.randomUUID().toString();
        var qrcode = qrCodeGenerator.generateQRCode(sessionCode, width, height);

        var session = Session.builder()
                .sessionCode(sessionCode)
                .name(request.name())
                .active(LocalDateTime.now().isAfter(request.startTime()) || LocalDateTime.now().isEqual(request.startTime()))
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        repository.save(session);
        sessionSchedulerService.scheduleSessionActivation(session);
        sessionSchedulerService.scheduleSessionInvalidation(session);
        return qrcode.toByteArray();

    }

    public List<Session> getAllSessions() {
        return repository.findAll();
    }

    public Session getSessionById(Integer sessionId) {
        return repository.findById(sessionId).orElseThrow(
                () -> new ResourceNotFoundException("Session not found")
        );
    }

    public SuccessResponse deleteSession(Integer sessionId) {
        var session = getSessionById(sessionId);
        repository.delete(session);
        return SuccessResponse.builder()
                .message("Session deleted successfully")
                .success(true)
                .build();
    }

    public SuccessResponse updateSession(Integer sessionId, UpdateSessionRequest request) {
        var session = getSessionById(sessionId);

        if (request.name() != null)
            session.setName(request.name());
        if (request.startTime() != null)
            session.setStartTime(request.startTime());
        if (request.endTime() != null)
            session.setEndTime(request.endTime());

        repository.save(session);
        sessionSchedulerService.scheduleSessionActivation(session);
        sessionSchedulerService.scheduleSessionInvalidation(session);

        return SuccessResponse.builder()
                .success(true)
                .message("Session updated successfully")
                .build();
    }
}
