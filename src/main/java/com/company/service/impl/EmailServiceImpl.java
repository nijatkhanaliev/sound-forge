package com.company.service.impl;

import com.company.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    @Override
    public void sendEmail(String to, String username, String emailTemplateName,
                          String subject, String activationCode)
            throws MessagingException {

        final String templateName;
        if (!StringUtils.hasText(emailTemplateName)) {
            templateName = "activate_account";
        } else {
            templateName = emailTemplateName;
        }

        log.info("Creating mimeMessage, templateName: {}",templateName);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        String template = templateEngine.process(templateName, context);

        helper.setFrom("khanalievn@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(template, true);

        mailSender.send(mimeMessage);
    }
}
