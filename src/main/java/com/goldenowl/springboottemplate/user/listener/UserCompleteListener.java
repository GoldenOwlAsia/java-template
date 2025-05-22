package com.goldenowl.springboottemplate.user.listener;

import com.goldenowl.springboottemplate.email.dto.CompleteUserMailDTO;
import com.goldenowl.springboottemplate.email.service.MailService;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCompleteListener {

    private final MailService mailService;

    @Async
    @PostPersist
    public void onSave(UserEntity entity) {
        mailService.sendCompleteUserMail(CompleteUserMailDTO.builder()
                .email(entity.getEmail())
                .name(entity.getName())
                .username(entity.getUsername())
                .createdAt(entity.getCreatedAt())
                .build());
    }
}
