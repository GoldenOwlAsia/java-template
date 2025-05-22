package com.goldenowl.springboottemplate.auth.repository;

import com.goldenowl.springboottemplate.auth.entity.SignUpEntity;
import com.goldenowl.springboottemplate.auth.enumeration.SignUpStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SignUpRepository extends JpaRepository<SignUpEntity, String> {
    boolean existsByUsernameAndStatusIn(String username, List<SignUpStatus> validStatuses);

    boolean existsByEmailAndStatusIn(String email, List<SignUpStatus> validStatuses);

    Optional<SignUpEntity> findByUsernameAndStatusIn(String username, List<SignUpStatus> pending);

    Optional<SignUpEntity> findByCurrentVerificationTokenAndStatusIn(String token, List<SignUpStatus> pending);
}
