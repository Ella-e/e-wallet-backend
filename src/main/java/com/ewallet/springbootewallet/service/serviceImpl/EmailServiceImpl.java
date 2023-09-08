package com.ewallet.springbootewallet.service.serviceImpl;


import com.ewallet.springbootewallet.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Async
   public void sendConfirmationToken(String name, String to, String token) {
        try {
            String messageBody = "Hello " + name + ",\n\nYour new account has been created. Please click the link below to verify your account. \n\n" +
                    "http://localhost:8081/user/activate?token=" + token + "\n\nThe support Team";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("New User Account Verification");
            message.setFrom(from);
            message.setTo(to);
            message.setText(messageBody);
            emailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }
    @Async
    public void sendVerificationCode(String name, String to, String code) {
        try {
            String messageBody = "Hello " + name + ",\n\nThere is an attempt to reset your password. Verification code is:\n\n" +
                    code + "\n\nThe support Team";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("Reset password verification");
            message.setFrom(from);
            message.setTo(to);
            message.setText(messageBody);
            emailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }
}
