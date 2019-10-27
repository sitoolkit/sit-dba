package io.sitoolkit.refimpl.db.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.MetaAttribute;

public class CustomReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

  private static final String CLASS_SUFFIX_MULTIPLE = "Entities";
  private static final String CLASS_SUFFIX = "Entity";

  public CustomReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
    super(delegate);
  }

  @Override
  public String tableToClassName(TableIdentifier tableIdentifier) {
    return super.tableToClassName(tableIdentifier) + CLASS_SUFFIX;
  }

  @Override
  public boolean excludeColumn(TableIdentifier identifier, String columnName) {
    switch (columnName.toLowerCase()) {
      case "created_date":
      case "created_by":
      case "updated_date":
      case "updated_by":
      case "version":
        return true;
      default:
        return false;
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Map tableToMetaAttributes(TableIdentifier tableIdentifier) {
    Map map = new HashMap<>();
    addMeta(map, "use-in-equals", "true");
    addMeta(map, "use-in-tostring", "true");
    addMeta(map, "extends", "BaseEntity");
    addMeta(map, "extra-import", "io.sitoolkit.refimpl.db.entity.infra.jpa.BaseEntity");

    return map;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void addMeta(Map map, String name, String... values) {
    MetaAttribute attr = new MetaAttribute(name);
    for (String value : values) {
      attr.addValue(value);
    }
    map.put(name, attr);
  }

  @Override
  public String foreignKeyToCollectionName(String keyname, TableIdentifier fromTable,
      List<?> fromColumns, TableIdentifier referencedTable, List<?> referencedColumns,
      boolean uniqueReference) {
    String defaultName = super.foreignKeyToCollectionName(keyname, fromTable, fromColumns,
        referencedTable, referencedColumns, uniqueReference);

    int suffixIndex = defaultName.indexOf(CLASS_SUFFIX_MULTIPLE);
    char charBeforeSuffix = defaultName.charAt(suffixIndex - 1);
    String replacement = charBeforeSuffix == 's' ? "es" : "s";

    return defaultName.replace(CLASS_SUFFIX_MULTIPLE, replacement);
  }

  @Override
  public String foreignKeyToEntityName(String keyname, TableIdentifier fromTable,
      List<?> fromColumnNames, TableIdentifier referencedTable, List<?> referencedColumnNames,
      boolean uniqueReference) {
    return super.foreignKeyToEntityName(keyname, fromTable, fromColumnNames, referencedTable,
        referencedColumnNames, uniqueReference).replace(CLASS_SUFFIX, "");
  }

  @Override
  public String foreignKeyToManyToManyName(ForeignKey fromKey, TableIdentifier middleTable,
      ForeignKey toKey, boolean uniqueReference) {
    return super.foreignKeyToManyToManyName(fromKey, middleTable, toKey, uniqueReference)
        .replace(CLASS_SUFFIX, "");
  }

  @Override
  public String foreignKeyToInverseEntityName(String keyname, TableIdentifier fromTable,
      List<?> fromColumnNames, TableIdentifier referencedTable, List<?> referencedColumnNames,
      boolean uniqueReference) {
    return super.foreignKeyToInverseEntityName(keyname, fromTable, fromColumnNames, referencedTable,
        referencedColumnNames, uniqueReference).replace(CLASS_SUFFIX, "");
  }

}
