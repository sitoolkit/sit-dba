package io.sitoolkit.dba.domain.sample;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainRepository extends CrudRepository<MainEntity, String> {

  List<MainEntity> findByName(String name);
}
