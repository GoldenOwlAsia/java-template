package com.goldenowl.springboottemplate.user.listener;

import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEntityListener {

  private final UserMailDispatcher userMailDispatcher;

  @PostPersist
  public void onPersist(UserEntity user) {
    if (user.getStatus() == UserStatus.PENDING) {
      userMailDispatcher.sendVerificationMail(user);
      return;
    }
    if (user.getStatus() == UserStatus.ACTIVE) {
      userMailDispatcher.sendCompleteMail(user);
    }
  }

  @PostUpdate
  public void onUpdate(UserEntity user) {
    if (user.getStatus() == UserStatus.PENDING) {
      userMailDispatcher.sendVerificationMail(user);
    }
  }
}
