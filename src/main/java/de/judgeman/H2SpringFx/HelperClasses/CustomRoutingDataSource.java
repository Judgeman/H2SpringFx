package de.judgeman.H2SpringFx.HelperClasses;

import de.judgeman.H2SpringFx.H2SpringFxApplication;
import de.judgeman.H2SpringFx.Model.DatabaseConnection;
import de.judgeman.H2SpringFx.Services.SettingService;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul Richter on Sat 05/09/2020
 */
public class CustomRoutingDataSource extends AbstractRoutingDataSource {

    private final static String CONFIG_DATASOURCE_SETTINGS_PATH_PREFIX = "jdbc:h2:file:";
    private final static String CONFIG_DATASOURCE_SETTINGS_PATH = "./settings/settings";
    private final static String CONFIG_DATASOURCE_SETTINGS_DRIVER_CLASSNAME = "org.h2.Driver";
    private final static String CONFIG_DATASOURCE_SETTINGS_SQL_DIALECT = "org.hibernate.dialect.H2Dialect";
    private final static String CONFIG_DATASOURCE_SETTINGS_DRIVER_USERNAME = "SA";
    private final static String CONFIG_DATASOURCE_SETTINGS_DRIVER_PASSWORD = "";

    private final Map<String, DataSourceDatabaseConnectionTuple> targetDataSources;

    private String currentDataSourceName;

    private String currentDialect;

    public CustomRoutingDataSource() throws SQLException {
        targetDataSources = new HashMap<>();

        currentDataSourceName = SettingService.NAME_SETTING_DATASOURCE;
        initSettingsDatasource();
    }

    public DataSource getDataSource(String lookUpKey) {
        if (targetDataSources.containsKey(lookUpKey)) {
            return targetDataSources.get(lookUpKey).dataSource;
        }

        return null;
    }

    public DataSourceDatabaseConnectionTuple getDataSourceDatabaseConnectionTuple(String lookUpKey) {
        if (targetDataSources.containsKey(lookUpKey)) {
            return targetDataSources.get(lookUpKey);
        }

        return null;
    }

    public DataSource getCurrentDataSource() {
        return getDataSource(currentDataSourceName);
    }

    private void initSettingsDatasource() throws SQLException {
        DatabaseConnection databaseConnection = createSettingDatabaseConnection();
        DataSource dataSource = createSettingsDataSource();
        DataSourceDatabaseConnectionTuple tuple = new DataSourceDatabaseConnectionTuple();

        tuple.databaseConnection = databaseConnection;
        tuple.dataSource = dataSource;

        targetDataSources.put(SettingService.NAME_SETTING_DATASOURCE, tuple);
    }

    private DatabaseConnection createSettingDatabaseConnection() {
        DatabaseConnection databaseConnection = new DatabaseConnection();

        databaseConnection.setUsername(CONFIG_DATASOURCE_SETTINGS_DRIVER_USERNAME);
        databaseConnection.setPassword(CONFIG_DATASOURCE_SETTINGS_DRIVER_PASSWORD);
        databaseConnection.setJdbcConnectionPrefix(CONFIG_DATASOURCE_SETTINGS_PATH_PREFIX);
        databaseConnection.setJdbcConnectionPath(CONFIG_DATASOURCE_SETTINGS_PATH);
        databaseConnection.setSqlDialect(CONFIG_DATASOURCE_SETTINGS_SQL_DIALECT);

        return databaseConnection;
    }

    private DataSource createSettingsDataSource() {
        return createNewDataSource(CONFIG_DATASOURCE_SETTINGS_DRIVER_CLASSNAME,
                String.format("%s%s", CONFIG_DATASOURCE_SETTINGS_PATH_PREFIX, CONFIG_DATASOURCE_SETTINGS_PATH),
                CONFIG_DATASOURCE_SETTINGS_DRIVER_USERNAME,
                CONFIG_DATASOURCE_SETTINGS_DRIVER_PASSWORD);
    }

    public DataSource createNewDataSource(String driverClassName,
                                          String dataSourceUrl,
                                          String username,
                                          String password) {

        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    public void registerNewDataSource(String dataSourceName, DataSourceDatabaseConnectionTuple tupel) {
        targetDataSources.put(dataSourceName, tupel);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return currentDataSourceName;
    }

    @Override
    protected DataSource determineTargetDataSource() {
        DataSourceDatabaseConnectionTuple tuple = targetDataSources.get(currentDataSourceName);
        String newDialect = tuple.databaseConnection.getSqlDialect();

        if (!newDialect.equals(currentDialect)) {
            currentDialect = newDialect;
            H2SpringFxApplication.testSettingDialect(newDialect);
        }

        return tuple.dataSource;
    }

    public void setCurrentDataSourceName(String currentDataSourceName) {
        this.currentDataSourceName = currentDataSourceName;
    }

    public String getCurrentDataSourceName() {
        return currentDataSourceName;
    }

    public Map<Object, Object> getTargetDataSources() {
        Map<Object, Object> newMap = new HashMap<>();

        for (String key : targetDataSources.keySet()) {
            DataSourceDatabaseConnectionTuple tuple = targetDataSources.get(key);
            newMap.put(key, tuple.dataSource);
        }

        return newMap;
    }
}
