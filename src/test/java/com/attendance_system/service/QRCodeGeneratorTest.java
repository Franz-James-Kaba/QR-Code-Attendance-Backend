package com.attendance_system.service;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QRCodeGeneratorTest {

    private final QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();

    @Test
    void testGenerateQRCode_ReturnsValidPngImage() throws WriterException, IOException {
        String sessionCode = "test-session-123";
        int width = 200;
        int height = 200;

        ByteArrayOutputStream outputStream = qrCodeGenerator.generateQRCode(sessionCode, width, height);

        assertNotNull(outputStream, "Output stream should not be null");
        assertTrue(outputStream.size() > 0, "Output stream should not be empty");

        // Check if the output is a valid PNG image
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        BufferedImage image = ImageIO.read(inputStream);

        assertNotNull(image, "Generated QR code is not a valid image");
        assertEquals(width, image.getWidth(), "Image width should match");
        assertEquals(height, image.getHeight(), "Image height should match");
    }

    @Test
    void testGenerateQRCode_InvalidSize_ThrowsException() {
        String sessionCode = "test-session-123";
        int width = 0;
        int height = 0;

        assertThrows(IllegalArgumentException.class, () -> {
            qrCodeGenerator.generateQRCode(sessionCode, width, height);
        });
    }
}
