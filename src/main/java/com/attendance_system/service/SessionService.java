package com.attendance_system.service;

import com.attendance_system.model.Session;
import com.attendance_system.repository.SessionRepository;
import com.attendance_system.request.GenerateSessionRequest;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository repository;
    private final QRCodeGenerator qrCodeGenerator;

    public byte[] generateQRCode(GenerateSessionRequest request, int width, int height) throws IOException, WriterException {
        var sessionCode = UUID.randomUUID().toString();
        var qrcode = qrCodeGenerator.generateQRCode(sessionCode, width, height);

        var session = Session.builder()
                .sessionCode(sessionCode)
                .active(LocalDateTime.now().isAfter(request.startTime()) || LocalDateTime.now().isEqual(request.startTime()))
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();
        repository.save(session);

        return qrcode.toByteArray();

    }
}
