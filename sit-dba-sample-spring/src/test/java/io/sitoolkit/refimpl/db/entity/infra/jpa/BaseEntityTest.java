package io.sitoolkit.refimpl.db.entity.infra.jpa;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.sitoolkit.refimpl.db.domain.persion.PersonEntity;

@RunWith(SpringRunner.class)
@ContextConfiguration
@DataJpaTest
public class BaseEntityTest {

  @Autowired
  EntityManager em;

  @Test
  public void test() {
    em.persist(new PersonEntity(1, "entity"));
    em.flush();

    PersonEntity entity = em.find(PersonEntity.class, 1, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    entity.setName("entity1");

    em.flush();

  }
}
