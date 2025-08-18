package com.goldenowl.ticketbooking.service.impl;

import com.goldenowl.ticketbooking.constant.AttributeConstant;
import com.goldenowl.ticketbooking.dto.request.CompleteUserMailDTO;
import com.goldenowl.ticketbooking.dto.request.VerifyUserMailDTO;
import com.goldenowl.ticketbooking.service.MailService;
import com.goldenowl.ticketbooking.template.AbstractMailHandler;
import com.goldenowl.ticketbooking.template.CompleteUserMailHandler;
import com.goldenowl.ticketbooking.template.VerifyUserMailHandler;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String senderMail;

    MailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine,
                    @Value("${spring.mail.username}") String senderMail) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.senderMail = senderMail;
    }

    @Override
    public void sendVerifyUserMail(VerifyUserMailDTO mailDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, mailDTO.getName());
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, mailDTO.getEmail());
        variables.put(AttributeConstant.VERIFY_TOKEN_ATTRIBUTE, mailDTO.getVerifyToken());
        variables.put(AttributeConstant.EXPIRED_DATE_ATTRIBUTE, mailDTO.getExpiredDate());

        AbstractMailHandler mailHandler = new VerifyUserMailHandler(
                mailSender, templateEngine, variables, senderMail, mailDTO.getEmail()
        );
        sendMail(mailHandler);
    }

    @Override
    public void sendCompleteUserMail(CompleteUserMailDTO mailDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, mailDTO.getName());
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, mailDTO.getEmail());
        variables.put(AttributeConstant.USERNAME_ATTRIBUTE, mailDTO.getUsername());
        variables.put(AttributeConstant.CREATED_AT_ATTRIBUTE, mailDTO.getCreatedAt());

        AbstractMailHandler mailHandler = new CompleteUserMailHandler(
                mailSender, templateEngine, variables, senderMail, mailDTO.getEmail()
        );
        sendMail(mailHandler);
    }

    private void sendMail(AbstractMailHandler mailHandler) {
        try {
            mailHandler.send();
        } catch (MessagingException e) {
            log.error(e.toString(), e);
        }
    }
}
