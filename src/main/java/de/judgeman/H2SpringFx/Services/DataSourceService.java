package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.Core.Configuration.MainRepositoryConfiguration;
import de.judgeman.H2SpringFx.HelperClasses.DataSourceDatabaseConnectionTuple;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * Created by Paul Richter on Tue 08/09/2020
 */
@Service
public class DataSourceService {

    private final Logger logger = LogService.getLogger(this.getClass());

    @Value("${database.primary.changelog.path}")
    private String liquibaseChangeLogPath;

    @Value("${spring.liquibase.database-change-log-lock-table}")
    private String liquibaseChangeLockTable;

    @Value("${spring.liquibase.database-change-log-table}")
    private String liquibaseChangeLogTable;

    @Autowired
    private SettingService settingService;

    public void initializePrimaryDataSource(DatabaseConnection databaseConnection) throws LiquibaseException {
        DataSource dataSource = MainRepositoryConfiguration.customRoutingDataSource.createNewDataSource(databaseConnection.getDriverClassName(),
                String.format("%s%s", databaseConnection.getJdbcConnectionPrefix(), databaseConnection.getJdbcConnectionPath()),
                databaseConnection.getUsername(),
                databaseConnection.getPassword());

        dBMigration(dataSource);

        DataSourceDatabaseConnectionTuple newTuple = new DataSourceDatabaseConnectionTuple();
        newTuple.dataSource = dataSource;
        newTuple.databaseConnection = databaseConnection;

        MainRepositoryConfiguration.customRoutingDataSource.registerNewDataSource(SettingService.NAME_PRIMARY_DATASOURCE, newTuple);
    }

    public void setCurrentDataSourceName(String datasourceName) {
        MainRepositoryConfiguration.customRoutingDataSource.setCurrentDataSourceName(datasourceName);
    }

    public DataSource createNewDataSource(String driverClassName, String path, String username, String password) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(path);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    private void dBMigration(DataSource dataSource) throws LiquibaseException {
        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseChangeLogPath);
        liquibase.setDatabaseChangeLogLockTable(liquibaseChangeLockTable);
        liquibase.setDatabaseChangeLogTable(liquibaseChangeLogTable);
        liquibase.setDropFirst(false);
        liquibase.setTestRollbackOnUpdate(true);
        liquibase.setShouldRun(true);

        liquibase.afterPropertiesSet();
    }
}
