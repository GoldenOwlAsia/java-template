package com.goldenowl.springboottemplate.user.repository;

import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

  @EntityGraph(attributePaths = {"roles", "roles.permissions"})
  Optional<UserEntity> findByUsernameAndStatus(String username, UserStatus status);

  Optional<UserEntity> findByUsername(String username);

  Optional<UserEntity> findByCurrentVerificationTokenAndStatus(String token, UserStatus status);

  Optional<UserEntity> findByUsernameAndStatusIn(String username, Iterable<UserStatus> statuses);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<UserEntity> findByEmail(String email);
}
