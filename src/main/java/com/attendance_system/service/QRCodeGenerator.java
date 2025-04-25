package com.attendance_system.service;

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.google.zxing.BarcodeFormat.QR_CODE;

@Service
public class QRCodeGenerator {

    public ByteArrayOutputStream generateQRCode(String sessionCode, int width, int height) throws WriterException, IOException {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Width and height must be positive");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(sessionCode, QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream;
    }

}
