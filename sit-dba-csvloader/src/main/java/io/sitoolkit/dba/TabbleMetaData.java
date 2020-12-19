package io.sitoolkit.dba;

import java.util.HashMap;
import java.util.Map;

public class TabbleMetaData {

  private Map<String, Integer> map = new HashMap<>();

  public int getDataType(String columnName) {
    return map.get(columnName);
  }

  public void addDataType(String columnName, int dataType) {
    map.put(columnName, dataType);
  }
}
