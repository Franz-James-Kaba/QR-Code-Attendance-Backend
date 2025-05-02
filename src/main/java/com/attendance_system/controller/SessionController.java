package com.attendance_system.controller;

import com.attendance_system.request.GenerateSessionRequest;
import com.attendance_system.response.QRCodeResponse;
import com.attendance_system.service.SessionService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.IMAGE_PNG;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('qrcode:generate')")
public class SessionController {
    private final SessionService service;

    @PostMapping("/generate-qrcode")
    public ResponseEntity<QRCodeResponse> generateQRCode(@RequestBody @Valid GenerateSessionRequest request,
                                                         @RequestParam(defaultValue = "250") int width,
                                                         @RequestParam(defaultValue = "250") int height
    ) throws IOException, WriterException
    {
        return ResponseEntity.ok()
                .contentType(IMAGE_PNG)
                .header(CONTENT_DISPOSITION, "attachment; filename=qrcode.png")
                .body(service.generateQRCode(request, width, height));
    }

}
