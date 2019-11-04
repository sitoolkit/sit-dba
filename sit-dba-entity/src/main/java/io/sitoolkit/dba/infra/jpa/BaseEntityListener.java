package io.sitoolkit.dba.infra.jpa;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This implementation is intended to track who and when create or update
 * Entity, but not enough for production usage so DO NOT USE this.
 * 
 * If your application is based on Jakarta EE, try Apache DeltasSike and its
 * Auditing functionality. https://deltaspike.apache.org/documentation/data.html
 * 
 * Or based on Spring, try Spring Data JPA which also supports Auditing.
 * https://docs.spring.io/spring-data/jpa/docs/2.2.0.RELEASE/reference/html/#auditing
 *
 */
public class BaseEntityListener {

  @PrePersist
  public void prePresist(BaseEntity entity) {
    LocalDateTime now = LocalDateTime.now();
    entity.setCreatedDate(now);
    entity.setUpdatedDate(now);

    entity.setCreatedBy("system");
    entity.setUpdatedBy("system");
  }

  @PreUpdate
  public void preUpdate(BaseEntity entity) {
    entity.setUpdatedDate(LocalDateTime.now());
    entity.setUpdatedBy("system");
  }
}
