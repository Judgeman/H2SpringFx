package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.HelperClasses.CustomRoutingDataSource;
import de.judgeman.H2SpringFx.HelperClasses.DataSourceDatabaseConnectionTupel;
import de.judgeman.H2SpringFx.Model.DatabaseConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;

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

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Autowired
    CustomRoutingDataSource customRoutingDataSource;

    public void checkSettingsDatasourceAndInitIfNeeded() throws SQLException {
        customRoutingDataSource.setCurrentDataSourceName(SettingService.NAME_SETTING_DATASOURCE);
        DataSource dataSource = customRoutingDataSource.getCurrentDataSource();

        assert dataSource != null;

        if (IsSchemaInitialisationForSettingsDBNeeded()) {
            InitSettingsSchema(dataSource);
        }
    }

    public void initializePrimaryDataSource(DatabaseConnection databaseConnection) {
        DataSource dataSource = customRoutingDataSource.createNewDataSource(databaseConnection.getDriverClassName(),
                String.format("%s%s", databaseConnection.getJdbcConnectionPrefix(), databaseConnection.getJdbcConnectionPath()),
                databaseConnection.getUsername(),
                databaseConnection.getPassword());

        DataSourceDatabaseConnectionTupel newTupel = new DataSourceDatabaseConnectionTupel();
        newTupel.dataSource = dataSource;
        newTupel.databaseConnection = databaseConnection;

        customRoutingDataSource.registerNewDataSource(SettingService.NAME_PRIMARY_DATASOURCE, newTupel);
    }

    public void setCurrentDataSourceNameAndDialect(String datasourceName) {
        customRoutingDataSource.setCurrentDataSourceName(datasourceName);
        DataSourceDatabaseConnectionTupel tuple = customRoutingDataSource.getDataSourceDatabaseConnectionTuple(datasourceName);
        if (tuple == null) {
            return;
        }

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", tuple.databaseConnection.getSqlDialact());

        sessionFactory.setHibernateProperties(properties);
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
        // TODO: set the datasource dialect
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
        return customRoutingDataSource.getDataSource(SettingService.NAME_PRIMARY_DATASOURCE) != null;
    }
}
