package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.Core.Configuration.MainRepositoryConfiguration;
import de.judgeman.H2SpringFx.HelperClasses.DataSourceDatabaseConnectionTuple;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by Paul Richter on Tue 08/09/2020
 */
@Service
public class DataSourceService {

    private final Logger logger = LogService.getLogger(this.getClass());

    public final String DATABASE_SCHEMA_CONFIG_FILE_SETTINGS = "config/databaseSchema_settings_create.sql";
    public final String DATABASE_SCHEMA_CONFIG_FILE_MODEL = "config/databaseSchema_model_create.sql";
    public final String DATABASE_SCHEMA_CONFIG_FILE_DROP_ALL_TABLE = "config/databaseSchema_drop.sql";

    @Autowired
    private SettingService settingService;

    public void initializePrimaryDataSource(DatabaseConnection databaseConnection) {
        DataSource dataSource = MainRepositoryConfiguration.customRoutingDataSource.createNewDataSource(databaseConnection.getDriverClassName(),
                String.format("%s%s", databaseConnection.getJdbcConnectionPrefix(), databaseConnection.getJdbcConnectionPath()),
                databaseConnection.getUsername(),
                databaseConnection.getPassword());

        DataSourceDatabaseConnectionTuple newTuple = new DataSourceDatabaseConnectionTuple();
        newTuple.dataSource = dataSource;
        newTuple.databaseConnection = databaseConnection;

        MainRepositoryConfiguration.customRoutingDataSource.registerNewDataSource(SettingService.NAME_PRIMARY_DATASOURCE, newTuple);
    }

    public void setCurrentDataSourceName(String datasourceName) {
        MainRepositoryConfiguration.customRoutingDataSource.setCurrentDataSourceName(datasourceName);
    }

    private boolean IsSchemaInitialisationForSettingsDBNeeded() {
        try {
            return settingService.loadSetting(SettingService.SETTINGS_DATABASE_INIT_KEY) == null;
        } catch (Exception ex) {
            logger.info("Error on reading from settings db: " + ex.getMessage());

            return true;
        }
    }

    private void InitSettingsSchema(DataSource dataSource) throws SQLException {
        logger.info("Try to init settings database structure");

        executeDatabaseSchemaScript(dataSource, DATABASE_SCHEMA_CONFIG_FILE_DROP_ALL_TABLE);
        executeDatabaseSchemaScript(dataSource, DATABASE_SCHEMA_CONFIG_FILE_SETTINGS);

        settingService.saveSetting(SettingService.SETTINGS_DATABASE_INIT_KEY,
                                   String.format("Init performed on %s", java.time.LocalDateTime.now()));
    }

    private void executeDatabaseSchemaScript(DataSource dataSource, String pathForResource) throws SQLException {
        URL databaseSchemaScriptUrl = getClass().getClassLoader().getResource(pathForResource);

        assert databaseSchemaScriptUrl != null;

        Resource databaseSchemaScript = new FileUrlResource(databaseSchemaScriptUrl);
        ScriptUtils.executeSqlScript(dataSource.getConnection(), databaseSchemaScript);
    }

    public DataSource createNewDataSource(String driverClassName, String path, String username, String password) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(path);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    public boolean isPrimaryDataSourceAvailable() {
        return MainRepositoryConfiguration.customRoutingDataSource.getDataSource(SettingService.NAME_PRIMARY_DATASOURCE) != null;
    }
}
