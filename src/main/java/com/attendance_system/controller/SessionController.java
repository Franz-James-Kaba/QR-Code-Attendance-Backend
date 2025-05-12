package com.attendance_system.controller;

import com.attendance_system.model.Session;
import com.attendance_system.request.GenerateSessionRequest;
import com.attendance_system.request.UpdateSessionRequest;
import com.attendance_system.response.SuccessResponse;
import com.attendance_system.service.SessionService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.IMAGE_PNG;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('qrcode:generate')")
public class SessionController {
    private final SessionService service;

    @PostMapping("/generate-qrcode")
    public ResponseEntity<byte[]> generateQRCode(@RequestBody @Valid GenerateSessionRequest request,
                                                         @RequestParam(defaultValue = "250") int width,
                                                         @RequestParam(defaultValue = "250") int height
    ) throws IOException, WriterException
    {
        return ResponseEntity.ok()
                .contentType(IMAGE_PNG)
                .header(CONTENT_DISPOSITION, "attachment; filename=qrcode.png")
                .body(service.generateQRCode(request, width, height));
    }

    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        return ResponseEntity.ok(service.getAllSessions());
    }

    @GetMapping("{sessionId}")
    public ResponseEntity<Session> getSessionById(@PathVariable("sessionId") Integer sessionId) {
        return ResponseEntity.ok(service.getSessionById(sessionId));
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Session>> getInactiveSessions() {
        return ResponseEntity.ok(service.getAllSessions().stream().filter(session -> !session.isActive()).toList());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Session>> getActiveSessions() {
        return ResponseEntity.ok(service.getAllSessions().stream().filter(Session::isActive).toList());
    }

    @PutMapping("{sessionId}")
    public ResponseEntity<SuccessResponse> updateSession(@PathVariable("sessionId") Integer sessionId, @RequestBody @Valid UpdateSessionRequest request) {
        return ResponseEntity.ok(
                service.updateSession(sessionId, request)
        );
    }

    @DeleteMapping("{sessionId}")
    public ResponseEntity<SuccessResponse> deleteSession(@PathVariable("sessionId") Integer sessionId) {
        return ResponseEntity.ok(
                service.deleteSession(sessionId)
        );
    }

}
