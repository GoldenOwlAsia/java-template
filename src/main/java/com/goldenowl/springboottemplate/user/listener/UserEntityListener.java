package com.goldenowl.springboottemplate.user.listener;

import com.goldenowl.springboottemplate.email.dto.CompleteUserMailDTO;
import com.goldenowl.springboottemplate.email.dto.VerifyUserMailDTO;
import com.goldenowl.springboottemplate.email.service.MailService;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEntityListener {

  private final MailService mailService;

  @Async
  @PostPersist
  public void onPersist(UserEntity user) {
    if (user.getStatus() == UserStatus.PENDING) {
      sendVerificationMail(user);
      return;
    }
    if (user.getStatus() == UserStatus.ACTIVE) {
      sendCompleteMail(user);
    }
  }

  @Async
  @PostUpdate
  public void onUpdate(UserEntity user) {
    if (user.getStatus() == UserStatus.PENDING) {
      sendVerificationMail(user);
    }
  }

  private void sendVerificationMail(UserEntity user) {
    log.info("Preparing to send verification email for user: {}", user.getEmail());
    try {
      mailService.sendVerifyUserMail(
          VerifyUserMailDTO.builder()
              .email(user.getEmail())
              .name(user.getName())
              .verifyToken(user.getCurrentVerificationToken())
              .expiredDate(user.getExpiredVerificationTokenDate())
              .build());
    } catch (Exception ex) {
      log.warn("Failed to send verification email to {}: {}", user.getEmail(), ex.getMessage());
    }
  }

  private void sendCompleteMail(UserEntity user) {
    try {
      mailService.sendCompleteUserMail(
          CompleteUserMailDTO.builder()
              .email(user.getEmail())
              .name(user.getName())
              .username(user.getUsername())
              .createdAt(user.getCreatedAt())
              .build());
    } catch (Exception ex) {
      log.warn("Failed to send welcome email to {}: {}", user.getEmail(), ex.getMessage());
    }
  }
}
