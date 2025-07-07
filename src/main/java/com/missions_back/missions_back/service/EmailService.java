package com.missions_back.missions_back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(List<String> to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendEmail(String to, String subject, String text) {
        sendEmail(List.of(to), subject, text);
    }
} 