package com.gerp.communication.service.mail;

import com.gerp.communication.pojo.mail.Mail;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    private static final String UTF_8 = "UTF-8";
    @Value("${redirect.url}")
    private String url;
    @Value("${spring.mail.username}")
    private String from;

    private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendSimpleMessage(Mail mail) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
//            helper.addAttachment("template-cover.png", new ClassPathResource("test.png"));

            Context context = new Context();
            context.setVariable("contextPath", url);
            context.setVariables((Map<String, Object>) mail.getModel());
            String html = templateEngine.process(mail.getTemplate(), context);

            helper.setTo(mail.getTo());
            helper.setText(html, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(from);

            emailSender.send(message);

            logger.info("none send successful------------------------------");
        } catch (Exception e) {
            logger.info("Email Send Exception " + e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    @Override
    public void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template, String... templateArgs) {

    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            // ...

            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file
                    = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);

            emailSender.send(message);
            // ...
        } catch (Exception ex) {

        }
    }
}