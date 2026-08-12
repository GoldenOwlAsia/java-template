package com.goldenowl.springboottemplate.app.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** Base superclass for JPA entities with a primary key and optimistic locking field. */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

  /** Primary key. */
  @Id private String id;

  /** Optimistic locking field (named {@code ol} because {@code version} is often overloaded). */
  @Version private long ol;

  /** Timestamp when the database record was inserted. */
  @CreatedDate private LocalDateTime createdAt;

  /** Username who created the entity. */
  @CreatedBy private String createdBy;

  /** Timestamp when the database record was updated the last time. */
  @LastModifiedDate private LocalDateTime lastModifiedAt;

  /** Username who modified the entity. */
  @LastModifiedBy private String lastModifiedBy;

  @PrePersist
  public void prePersist() {
    if (id == null) {
      id = UUID.randomUUID().toString();
    }
  }
}
