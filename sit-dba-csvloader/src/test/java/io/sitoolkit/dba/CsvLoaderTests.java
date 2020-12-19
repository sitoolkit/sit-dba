package io.sitoolkit.dba;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class CsvLoaderTests {

  @Test
  public void test() throws Exception {

    Properties prop = new Properties();
    prop.load(getClass().getResourceAsStream("/connection.properties"));

    Connection connection = DriverManager
        .getConnection(prop.getProperty("url"), prop.getProperty("user"), prop.getProperty("password"));

    TabbleMetaData metaData = CsvLoader.extractMetaData(connection, "person");

    assertEquals(Types.INTEGER, metaData.getDataType("id"));
    assertEquals(Types.VARCHAR, metaData.getDataType("name"));
  }
}
