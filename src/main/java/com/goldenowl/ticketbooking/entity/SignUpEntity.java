package com.goldenowl.ticketbooking.entity;

import com.goldenowl.ticketbooking.enumeration.SignUpStatus;
import com.goldenowl.ticketbooking.listener.SignUpEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "GO_SIGN_UP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(SignUpEntityListener.class)
public class SignUpEntity extends BaseEntity {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SignUpStatus status;

    @Column(nullable = false)
    private String currentVerificationToken;

    @Column(nullable = false)
    private LocalDateTime expiredVerificationTokenDate;
}
