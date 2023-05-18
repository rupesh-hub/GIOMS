package com.gerp.communication.service.mail;

import com.gerp.communication.pojo.mail.Mail;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.MessagingException;

public interface EmailService {
    void sendSimpleMessage(Mail mail) throws MessagingException;
    void sendSimpleMessageUsingTemplate(String to,
                                        String subject,
                                        SimpleMailMessage template,
                                        String... templateArgs);
    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);
}