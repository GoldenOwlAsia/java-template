package com.goldenowl.ticketbooking.listener;

import com.goldenowl.ticketbooking.entity.SignUpEntity;
import com.goldenowl.ticketbooking.dto.request.VerifyUserMailDTO;
import com.goldenowl.ticketbooking.service.MailService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.goldenowl.ticketbooking.enumeration.SignUpStatus.PENDING;

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
