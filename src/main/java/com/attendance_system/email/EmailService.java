package com.attendance_system.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final EmailConfiguration emailConfiguration;
    private static final String ENVELOPE_ICON = "envelope-icon";
    private static final String ENVELOPE_ICON_PATH = "static/images/envelope-icon.png";

    public void sendPasswordResetEmail(
            String to,
            String username,
            String token
    ) throws MessagingException {
        String templateName = "reset_password";
        String subject = "Password Reset";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("activation_code", token);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(emailConfiguration.getUsername());
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        ClassPathResource envelopeIcon = new ClassPathResource(ENVELOPE_ICON_PATH);
        helper.addInline(ENVELOPE_ICON, envelopeIcon);
        mailSender.send(mimeMessage);
    }

    public void sendMailWithTemporaryPassword(
            String to,
            String username,
            String password
    ) throws MessagingException {
        String templateName = "temporary_password";
        String subject = "Account Created Successfully";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("email", to);
        properties.put("temporary_password", password);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(emailConfiguration.getUsername());
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        ClassPathResource envelopeIcon = new ClassPathResource(ENVELOPE_ICON_PATH);
        helper.addInline(ENVELOPE_ICON, envelopeIcon);

        mailSender.send(mimeMessage);
    }

    public void sendReceptionCredentials(
            String recipient,
            String firstName,
            String receptionEmail,
            String password
    ) throws MessagingException {
        String templateName = "reception_credentials";
        String subject = "Reception Account Credentials";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("firstName", firstName);
        properties.put("receptionEmail", receptionEmail);
        properties.put("receptionPassword", password);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(emailConfiguration.getUsername());
        helper.setTo(recipient);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        ClassPathResource envelopeIcon = new ClassPathResource(ENVELOPE_ICON_PATH);
        helper.addInline(ENVELOPE_ICON, envelopeIcon);
        mailSender.send(mimeMessage);
    }
}
