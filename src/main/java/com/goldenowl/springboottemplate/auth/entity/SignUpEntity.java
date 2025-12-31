package com.goldenowl.springboottemplate.auth.entity;

import com.goldenowl.springboottemplate.app.entity.BaseEntity;
import com.goldenowl.springboottemplate.auth.enumeration.SignUpStatus;
import com.goldenowl.springboottemplate.auth.listener.SignUpEntityListener;
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

/**
 * @deprecated This entity is deprecated and will be removed in future versions.
 *
 * This class was originally introduced to separate the Sign-Up flow from the User domain
 * in order to handle verification, temporary credentials, and onboarding logic independently.
 *
 * Due to increasing complexity and overlap between SignUpEntity and UserEntity,
 * the sign-up data and lifecycle are now merged into the User entity to simplify
 * the domain model and reduce duplication.
 *
 * Please use {@code UserEntity} for all new implementations.
 */
@Deprecated
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
