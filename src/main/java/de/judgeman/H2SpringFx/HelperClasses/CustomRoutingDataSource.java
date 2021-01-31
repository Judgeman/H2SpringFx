package de.judgeman.H2SpringFx.HelperClasses;

import de.judgeman.H2SpringFx.Core.Configuration.MainRepositoryConfiguration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul Richter on Sat 05/09/2020
 */
public class CustomRoutingDataSource extends AbstractRoutingDataSource {

    private final Map<String, DataSourceDatabaseConnectionTuple> targetDataSources;

    private String currentDataSourceName;

    private String currentDialect;

    public CustomRoutingDataSource() {
        targetDataSources = new HashMap<>();
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
            MainRepositoryConfiguration.testSettingDialect(newDialect);
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
