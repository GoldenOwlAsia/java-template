package com.goldenowl.springboottemplate.user.entity;

import com.goldenowl.springboottemplate.app.entity.BaseEntity;
import com.goldenowl.springboottemplate.auth.entity.RoleEntity;
import com.goldenowl.springboottemplate.user.enumeration.UserStatus;
import com.goldenowl.springboottemplate.user.listener.UserEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Entity
@Table(name = "GO_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SoftDelete
@EntityListeners(UserEntityListener.class)
public class UserEntity extends BaseEntity {

  @Column(nullable = false)
  private String username;

  @Column private String password;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column private String oauthId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;

  @Column private String currentVerificationToken;

  @Column private LocalDateTime expiredVerificationTokenDate;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "GO_USER_ROLES",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles;
}
