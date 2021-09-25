package io.sitoolkit.dba.domain.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MainRepositoryTest {

  @Autowired MainRepository repository;

  @Test
  public void test() {
    MainEntity main = new MainEntity();
    main.setId(UUID.randomUUID().toString());
    main.setName("name");
    repository.save(main);

    List<MainEntity> mains = repository.findByName(main.getName());

    assertEquals(mains.size(), 1);
    assertEquals(mains.get(0).getId(), main.getId());
  }
}
