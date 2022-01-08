package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseType;
import de.judgeman.H2SpringFx.Setting.Model.SettingEntry;
import de.judgeman.H2SpringFx.Setting.Repository.DatabaseConnectionRepository;
import de.judgeman.H2SpringFx.Setting.Repository.SettingEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
@Service
public class SettingService {

    public static final String NAME_PRIMARY_DATASOURCE = "Primary";

    public static final String LANGUAGE_ENTRY_KEY = "currentLanguage";
    public static final String CURRENT_PRIMARY_DATABASE_CONNECTION_KEY = "currentDatabaseConnection";

    @Autowired
    private SettingEntryRepository settingEntryRepository;

    @Autowired
    private DatabaseConnectionRepository databaseConnectionRepository;

    public void saveSetting(String key, String value) {
        SettingEntry settingEntry = settingEntryRepository.findById(key).orElse(null);

        if(settingEntry == null) {
            settingEntry = new SettingEntry();
            settingEntry.setKey(key);
        }

        settingEntry.setValue(value);
        settingEntryRepository.save(settingEntry);
    }

    public String loadSetting(String key) {
        SettingEntry settingEntry = settingEntryRepository.findById(key).orElse(null);

        if(settingEntry == null) {
            return null;
        }

        return settingEntry.getValue();
    }

    public boolean deleteSetting(String key) {
        SettingEntry settingEntry = settingEntryRepository.findById(key).orElse(null);

        if(settingEntry != null) {
            settingEntryRepository.delete(settingEntry);
            return true;
        }

        return false;
    }

    public DatabaseConnection getDatabaseConnection(String name) {
        return databaseConnectionRepository.findById(name).orElse(null);
    }

    public List<DatabaseConnection> getAllDatabaseConnections() {
        return databaseConnectionRepository.findAll();
    }

    public boolean existAnyDatabaseConnections() {
        List<DatabaseConnection> databaseConnections = databaseConnectionRepository.findAll(PageRequest.of(0, 1));
        return databaseConnections.size() > 0;
    }

    public DatabaseConnection createNewDatabaseConnection(DatabaseType databaseType,
                                                          String driverClassName,
                                                          String sqlDialect,
                                                          String urlPrefix,
                                                          String urlPath,
                                                          String name,
                                                          String username,
                                                          String password) {
        DatabaseConnection databaseConnection = databaseConnectionRepository.findById(name).orElse(new DatabaseConnection());

        databaseConnection.setId(name);
        databaseConnection.setDatabaseType(databaseType);
        databaseConnection.setDriverClassName(driverClassName);
        databaseConnection.setSqlDialect(sqlDialect);
        databaseConnection.setJdbcConnectionPrefix(urlPrefix);
        databaseConnection.setJdbcConnectionPath(urlPath);
        databaseConnection.setUsername(username);
        databaseConnection.setPassword(password);

        return databaseConnection;
    }

    public void saveNewConnection(DatabaseConnection databaseConnection) {
        databaseConnectionRepository.save(databaseConnection);
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
        databaseConnectionRepository.delete(databaseConnection);
    }
}
