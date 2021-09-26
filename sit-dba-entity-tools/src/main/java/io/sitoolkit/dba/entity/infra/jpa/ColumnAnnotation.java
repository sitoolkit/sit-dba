package io.sitoolkit.dba.entity.infra.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class ColumnAnnotation {

  private String type;

  private Map<String, String> attributes = new HashMap<>();

  private List<String> columns = new ArrayList<>();

  public String build() {
    StringBuilder sb = new StringBuilder();

    sb.append("@");
    sb.append(type);
    sb.append("(");
    sb.append(
        attributes.entrySet().stream()
            .map(entry -> entry.getKey() + " = " + entry.getValue())
            .collect(Collectors.joining(",")));
    sb.append(")");

    return sb.toString();
  }
}
