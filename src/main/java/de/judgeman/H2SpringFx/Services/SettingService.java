package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.Model.DatabaseConnection;
import de.judgeman.H2SpringFx.Model.SettingEntry;
import de.judgeman.H2SpringFx.Repositories.DatabaseConnectionRepository;
import de.judgeman.H2SpringFx.Repositories.SettingEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
@Service
public class SettingService {

    public static final String NAME_SETTING_DATASOURCE = "Settings";
    public static final String NAME_PRIMARY_DATASOURCE = "Primary";

    public static final String LANGUAGE_ENTRY_KEY = "currentLanguage";
    public static final String USE_DIALOG_ENTRY_KEY = "useDialog";
    public static final String SETTINGS_DATABASE_INIT_KEY = "settingsDbInit";
    public static final String CURRENT_PRIMARY_DATABASE_CONNECTION_KEY = "currentDatabaseConnection";

    @Autowired
    private SettingEntryRepository settingEntryRepository;
    @Autowired
    private DatabaseConnectionRepository databaseConnectionRepository;
    @Autowired
    private DataSourceService dataSourceService;

    public void saveSetting(String key, String value) {
        dataSourceService.setCurrentDataSourceNameAndDialect(NAME_SETTING_DATASOURCE);
        SettingEntry settingEntry = settingEntryRepository.findById(key).orElse(null);

        if(settingEntry == null) {
            settingEntry = new SettingEntry();
            settingEntry.setKey(key);
        }

        settingEntry.setValue(value);
        settingEntryRepository.save(settingEntry);
    }

    public String loadSetting(String key) {
        dataSourceService.setCurrentDataSourceNameAndDialect(NAME_SETTING_DATASOURCE);
        SettingEntry settingEntry = settingEntryRepository.findById(key).orElse(null);

        if(settingEntry == null) {
            return null;
        }

        return settingEntry.getValue();
    }

    public boolean deleteSetting(String key) {
        dataSourceService.setCurrentDataSourceNameAndDialect(NAME_SETTING_DATASOURCE);
        SettingEntry settingEntry = settingEntryRepository.findById(key).orElse(null);

        if(settingEntry != null) {
            settingEntryRepository.delete(settingEntry);
            return true;
        }

        return false;
    }

    public DatabaseConnection getDatabaseConnection(String name) {
        dataSourceService.setCurrentDataSourceNameAndDialect(NAME_SETTING_DATASOURCE);
        return databaseConnectionRepository.findById(name).orElse(null);
    }

    public List<DatabaseConnection> getAllDatabaseConnections() {
        dataSourceService.setCurrentDataSourceNameAndDialect(NAME_SETTING_DATASOURCE);
        return databaseConnectionRepository.findAll();
    }

    public boolean existAnyDatabaseConnections() {
        dataSourceService.setCurrentDataSourceNameAndDialect(NAME_SETTING_DATASOURCE);
        List<DatabaseConnection> databaseConnections = databaseConnectionRepository.findAll(PageRequest.of(0, 1));
        return databaseConnections.size() > 0;
    }

    public DatabaseConnection saveNewConnection(String driverClassName, String sqlDialect, String urlPrefix, String urlPath, String name, String username, String password) {
        dataSourceService.setCurrentDataSourceNameAndDialect(NAME_SETTING_DATASOURCE);
        DatabaseConnection databaseConnection = new DatabaseConnection();

        databaseConnection.setId(name);
        databaseConnection.setDriverClassName(driverClassName);
        databaseConnection.setSqlDialact(sqlDialect);
        databaseConnection.setJdbcConnectionPrefix(urlPrefix);
        databaseConnection.setJdbcConnectionPath(urlPath);
        databaseConnection.setUsername(username);
        databaseConnection.setPassword(password);

        databaseConnectionRepository.save(databaseConnection);

        return databaseConnection;
    }

    public DatabaseConnection findCurrentDatabaseConnection(String currentDatabaseConnectionId, List<DatabaseConnection> allDatabaseConnections) {
        for (DatabaseConnection databaseConnection : allDatabaseConnections) {
            String id = databaseConnection.getId();
            if (id != null && id.equals(currentDatabaseConnectionId)) {
                return databaseConnection;
            }
        }

        return null;
    }

    public void deleteConnection(DatabaseConnection databaseConnection) {
        dataSourceService.setCurrentDataSourceNameAndDialect(NAME_SETTING_DATASOURCE);
        databaseConnectionRepository.delete(databaseConnection);
    }
}
