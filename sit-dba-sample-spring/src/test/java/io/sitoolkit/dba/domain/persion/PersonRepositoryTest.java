package io.sitoolkit.dba.domain.persion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration
@DataJpaTest
public class PersonRepositoryTest {

  @Autowired
  PersonRepository repository;

  @Test
  public void test() {
    PersonEntity person = new PersonEntity();
    person.setId(100);
    person.setName("name");
    repository.save(person);

    List<PersonEntity> persons = repository.findByName(person.getName());

    assertThat(persons.size(), is(1));
  }
}
