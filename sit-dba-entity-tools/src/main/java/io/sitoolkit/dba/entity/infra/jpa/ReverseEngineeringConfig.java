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

  private Map<String, String> buildTablePackageMap() {
    Map<String, String> tablePackageMap = new HashMap<>();

    for (Entry<String, List<String>> entry : packageMappings.entrySet()) {
      for (String value : entry.getValue()) {
        tablePackageMap.put(value, entry.getKey());
      }
    }

    return tablePackageMap;
  }

  public Optional<String> findPackageByTable(String tableName) {
    return Optional.ofNullable(getTablePackageMap().get(tableName));
  }

  public Optional<String> findJavaTypeByJdbcType(int sqlType) {
    return Optional.ofNullable(getJdbcJavaTypeMap().get(sqlType));
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
