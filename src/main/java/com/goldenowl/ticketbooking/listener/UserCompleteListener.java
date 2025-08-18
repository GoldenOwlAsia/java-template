package com.goldenowl.ticketbooking.listener;

import com.goldenowl.ticketbooking.dto.request.CompleteUserMailDTO;
import com.goldenowl.ticketbooking.service.MailService;
import com.goldenowl.ticketbooking.entity.UserEntity;
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
