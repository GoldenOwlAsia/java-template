package com.goldenowl.springboottemplate.auth.listener;

import com.goldenowl.springboottemplate.auth.entity.SignUpEntity;
import com.goldenowl.springboottemplate.email.dto.VerifyUserMailDTO;
import com.goldenowl.springboottemplate.email.service.MailService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.goldenowl.springboottemplate.auth.enumeration.SignUpStatus.PENDING;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignUpEntityListener {

    private final MailService mailService;

    @Async
    @PostPersist
    @PostUpdate
    public void onSave(SignUpEntity signUpEntity) {
        if (signUpEntity.getStatus() != PENDING) {
            return;
        }
        log.info("Preparing to send verification email for user: {}", signUpEntity.getEmail());
        mailService.sendVerifyUserMail(VerifyUserMailDTO.builder()
                .email(signUpEntity.getEmail())
                .name(signUpEntity.getName())
                .verifyToken(signUpEntity.getCurrentVerificationToken())
                .expiredDate(signUpEntity.getExpiredVerificationTokenDate())
                .build());
    }
}
