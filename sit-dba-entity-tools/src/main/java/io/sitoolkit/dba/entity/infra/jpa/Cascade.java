package io.sitoolkit.dba.entity.infra.jpa;

import lombok.Data;

@Data
public class Cascade {

  private String table;
  private String referencedTable;
  private String type;
}
