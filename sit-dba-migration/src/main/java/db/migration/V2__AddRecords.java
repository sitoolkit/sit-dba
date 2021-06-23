package db.migration;

import org.flywaydb.core.api.logging.Log;
import org.flywaydb.core.api.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import io.sitoolkit.csv.core.CsvLoader;

@SuppressWarnings("squid:S101")
public class V2__AddRecords extends BaseJavaMigration {

  private final Log log = LogFactory.getLog(V2__AddRecords.class);

  @Override
  public void migrate(Context ctx) throws Exception {
    CsvLoader.load(ctx.getConnection(), getClass(), log::info);
  }
  
}
