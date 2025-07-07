package com.company.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to,String username,String emailTemplateName,
                   String subject,String activationCode) throws MessagingException;
}
