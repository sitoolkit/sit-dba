package io.sitoolkit.dba.entity.infra.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.MetaAttribute;

public class ConfigurableReverseEngineeringStrategy extends DefaultReverseEngineeringStrategy {

  private ReverseEngineeringConfig config;

  private ReverseEngineeringConfig getConfig() {
    if (config == null) {
      config = ReverseEngineeringConfig.load();
    }
    return config;
  }

  @Override
  public String tableToClassName(TableIdentifier tableIdentifier) {
    String className = super.tableToClassName(tableIdentifier) + getConfig().getClassSuffix();
    Optional<String> packageOpt = getConfig().findPackageByTable(tableIdentifier.getName());
    return packageOpt.isEmpty() ? className : packageOpt.get() + "." + className;
  }

  @Override
  public boolean excludeTable(TableIdentifier tableIdentifier) {
    return getConfig().getExcludeTables().contains(tableIdentifier.getName().toLowerCase());
  }

  @Override
  public boolean excludeColumn(TableIdentifier identifier, String columnName) {
    return getConfig().getExcludeColumns().contains(columnName.toLowerCase());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Map tableToMetaAttributes(TableIdentifier tableIdentifier) {
    Map map = new HashMap<>();

    String baseClass = getConfig().getBaseClass();
    if (baseClass != null && !baseClass.isEmpty()) {
      addMeta(map, "extends", baseClass.substring(baseClass.lastIndexOf(".") + 1));
      addMeta(map, "extra-import", baseClass);
    }

    return map;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void addMeta(Map map, String name, String... values) {
    MetaAttribute attr = new MetaAttribute(name);
    for (String value : values) {
      attr.addValue(value);
    }
    map.put(name, attr);
  }

  @Override
  public String foreignKeyToCollectionName(
      String keyname,
      TableIdentifier fromTable,
      List<?> fromColumns,
      TableIdentifier referencedTable,
      List<?> referencedColumns,
      boolean uniqueReference) {

    return removePropertySuffix(
        super.foreignKeyToCollectionName(
            keyname, fromTable, fromColumns, referencedTable, referencedColumns, uniqueReference));
  }

  @Override
  public String foreignKeyToEntityName(
      String keyname,
      TableIdentifier fromTable,
      List<?> fromColumnNames,
      TableIdentifier referencedTable,
      List<?> referencedColumnNames,
      boolean uniqueReference) {
    return removeClassSuffix(
        super.foreignKeyToEntityName(
            keyname,
            fromTable,
            fromColumnNames,
            referencedTable,
            referencedColumnNames,
            uniqueReference));
  }

  @Override
  public String foreignKeyToManyToManyName(
      ForeignKey fromKey, TableIdentifier middleTable, ForeignKey toKey, boolean uniqueReference) {
    return removePropertySuffix(
        super.foreignKeyToManyToManyName(fromKey, middleTable, toKey, uniqueReference));
  }

  @Override
  public String foreignKeyToInverseEntityName(
      String keyname,
      TableIdentifier fromTable,
      List<?> fromColumnNames,
      TableIdentifier referencedTable,
      List<?> referencedColumnNames,
      boolean uniqueReference) {
    return removeClassSuffix(
        super.foreignKeyToInverseEntityName(
            keyname,
            fromTable,
            fromColumnNames,
            referencedTable,
            referencedColumnNames,
            uniqueReference));
  }

  @Override
  public String classNameToCompositeIdName(String className) {
    return removeClassSuffix(className) + "Id";
  }

  @Override
  public String columnToHibernateTypeName(
      TableIdentifier table,
      String columnName,
      int sqlType,
      int length,
      int precision,
      int scale,
      boolean nullable,
      boolean generatedIdentifier) {

    return getConfig()
        .findJavaTypeByJdbcType(sqlType)
        .orElseGet(
            () ->
                super.columnToHibernateTypeName(
                    table,
                    columnName,
                    sqlType,
                    length,
                    precision,
                    scale,
                    nullable,
                    generatedIdentifier));
  }

  private String removeClassSuffix(String className) {
    if (getConfig().getClassSuffix() == null || getConfig().getClassSuffix().isEmpty()) {
      return className;
    }

    return className.replace(getConfig().getClassSuffix(), "");
  }

  // @Override
  // public AssociationInfo foreignKeyToAssociationInfo(ForeignKey foreignKey) {
  //   DefaulAssociationInfo associationInfo = new DefaulAssociationInfo();

  //   return associationInfo;
  // }

  // @Override
  // public AssociationInfo foreignKeyToInverseAssociationInfo(ForeignKey foreignKey) {
  //   DefaulAssociationInfo associationInfo = new DefaulAssociationInfo();

  //   return associationInfo;
  // }

  private String removePropertySuffix(String propertyName) {
    if (getConfig().getClassSuffix() == null || getConfig().getClassSuffix().isEmpty()) {
      return propertyName;
    }

    String propertySuffix = pluralize(getConfig().getClassSuffix());
    return pluralize(propertyName.replace(propertySuffix, ""));
  }
}