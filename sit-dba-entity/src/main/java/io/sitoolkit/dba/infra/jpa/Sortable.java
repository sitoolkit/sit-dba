package io.sitoolkit.dba.infra.jpa;

public interface Sortable extends Comparable<Sortable> {

  public Integer getSortKey();

  @Override
  default int compareTo(Sortable o) {
    return Integer.compare(getSortKey(), o.getSortKey());
  }
}
