package com.gerp.communication.controller;

import com.gerp.communication.pojo.mail.Mail;
import com.gerp.communication.service.mail.EmailService;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping(value = "/email")
public class EmailController extends BaseController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * <p>
     * This method is used to send mail.
     * </p>
     *
     * @param mail
     * @return global response
     */
    @PostMapping("/send-message")
    ResponseEntity<?> sendMessage(@RequestBody Mail<?> mail) throws MessagingException {
        emailService.sendSimpleMessage(mail);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("email.sent"),
                        null
                )
        );
    }
}
