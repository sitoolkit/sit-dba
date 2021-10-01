package io.sitoolkit.dba.domain.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MainRepositoryTest {

  @Autowired MainRepository repository;

  @PersistenceContext EntityManager em;

  @Test
  void test() {
    MainEntity main = new MainEntity();
    main.setId(UUID.randomUUID().toString());
    main.setName("name");
    main.setColJsonb("{}");
    repository.save(main);

    List<MainEntity> mains = repository.findByName(main.getName());

    assertEquals(mains.size(), 1);
    assertEquals(mains.get(0).getId(), main.getId());
  }

  @Test
  void testCascade() {
    MainEntity main = new MainEntity();
    main.setId(UUID.randomUUID().toString());

    OneToManyEntity oneToMany = new OneToManyEntity();
    oneToMany.setId(UUID.randomUUID().toString());
    oneToMany.setMain(main);
    main.getOneToManies().add(oneToMany);

    repository.save(main);
    em.flush();
    em.clear();

    MainEntity savedMain = repository.findById(main.getId()).get();

    assertEquals(oneToMany.getId(), savedMain.getOneToManies().iterator().next().getId());
  }
}
