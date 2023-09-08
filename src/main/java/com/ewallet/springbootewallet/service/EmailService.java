package com.ewallet.springbootewallet.service;

public interface EmailService {
    void sendConfirmationToken(String to, String subject, String text);
    void sendVerificationCode(String name, String to, String code);
}
