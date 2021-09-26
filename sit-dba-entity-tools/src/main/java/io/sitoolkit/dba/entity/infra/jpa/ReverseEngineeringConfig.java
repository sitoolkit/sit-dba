package io.sitoolkit.dba.entity.infra.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;

@Data
public class ReverseEngineeringConfig {

  private String classSuffix = "";

  private String baseClass = "";

  private List<String> excludeTables = new ArrayList<>();

  private List<String> excludeColumns = new ArrayList<>();

  private Map<String, String> typeMappings = new HashMap<>();

  private Map<String, List<String>> packageMappings = new HashMap<>();

  private List<ColumnAnnotation> extraColumnAnnotations = new ArrayList<>();

  private List<Cascade> cascades = new ArrayList<>();

  /** key: table, value: package */
  @Getter(lazy = true)
  private final Map<String, String> tablePackageMap = buildTablePackageMap();

  /** key: java.sql.JDBCType.type, value: java class name (FQN) */
  @Getter(lazy = true)
  private final Map<Integer, String> jdbcJavaTypeMap =
      typeMappings.entrySet().stream()
          .collect(
              Collectors.toMap(
                  entry -> JDBCType.valueOf(entry.getKey()).getVendorTypeNumber(),
                  Entry::getValue));

  /** key: column (table.column) */
  @Getter(lazy = true)
  private final Map<String, List<ColumnAnnotation>> columnAnnotationMap =
      buildColumnAnnotationMap();

  /** key: table -> referencedTable */
  @Getter(lazy = true)
  private final Map<String, Cascade> cascadeMap = buildCascadeMap();

  private Map<String, Cascade> buildCascadeMap() {
    Map<String, Cascade> map = new HashMap<>();

    for (Cascade cascade : cascades) {
      map.put(cascade.getTable() + "->" + cascade.getReferencedTable(), cascade);
    }

    return map;
  }

  private Map<String, String> buildTablePackageMap() {
    Map<String, String> tablePackageMap = new HashMap<>();

    for (Entry<String, List<String>> entry : packageMappings.entrySet()) {
      for (String value : entry.getValue()) {
        tablePackageMap.put(value, entry.getKey());
      }
    }

    return tablePackageMap;
  }

  private Map<String, List<ColumnAnnotation>> buildColumnAnnotationMap() {
    Map<String, List<ColumnAnnotation>> map = new HashMap<>();

    for (ColumnAnnotation colAnn : extraColumnAnnotations) {
      for (String column : colAnn.getColumns()) {
        map.computeIfAbsent(column, key -> new ArrayList<>()).add(colAnn);
      }
    }

    return map;
  }

  public Optional<String> findPackageByTable(String tableName) {
    return Optional.ofNullable(getTablePackageMap().get(tableName));
  }

  public Optional<String> findJavaTypeByJdbcType(int sqlType) {
    return Optional.ofNullable(getJdbcJavaTypeMap().get(sqlType));
  }

  public List<ColumnAnnotation> findColumnAnnotations(String table, String column) {
    return getColumnAnnotationMap().getOrDefault(table + "." + column, new ArrayList<>());
  }

  public Optional<Cascade> findCascade(String table, String referencedTable) {
    return Optional.ofNullable(getCascadeMap().get(table + "->" + referencedTable));
  }

  public static ReverseEngineeringConfig load() {
    return load(Path.of("target/config.json"));
  }

  public static ReverseEngineeringConfig load(Path configPath) {

    if (!configPath.toFile().exists()) {
      return new ReverseEngineeringConfig();
    }

    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(configPath.toFile(), ReverseEngineeringConfig.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
