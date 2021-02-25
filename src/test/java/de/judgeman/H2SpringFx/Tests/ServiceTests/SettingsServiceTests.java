package de.judgeman.H2SpringFx.Tests.ServiceTests;

import de.judgeman.H2SpringFx.Services.SettingService;
import de.judgeman.H2SpringFx.Setting.Model.DatabaseConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class SettingsServiceTests {

    @Autowired
    private SettingService settingService;

    @Test
    public void saveAndLoadSettingsTest() {
        String key = "SettingService tested";
        String valueToSave = "true";

        settingService.saveSetting(key, valueToSave);

        String valueLoaded = settingService.loadSetting(key);
        Assertions.assertNotNull(valueLoaded);
        Assertions.assertEquals(valueToSave, valueLoaded);
    }

    @Test
    public void deleteSettingsTest() {
        String key = "Pauls Dog";
        String value = "Sam";

        settingService.saveSetting(key, value);

        String valueLoaded = settingService.loadSetting(key);
        Assertions.assertNotNull(valueLoaded);

        settingService.deleteSetting(key);

        valueLoaded = settingService.loadSetting(key);
        Assertions.assertNull(valueLoaded);
    }

    @Test
    public void nothingToDeleteSettingsTest() {
        String keyToDelete = "nothingToDelete";
        settingService.deleteSetting(keyToDelete);

        String valueLoaded = settingService.loadSetting(keyToDelete);
        Assertions.assertNull(valueLoaded);
    }

    @Test
    public void databaseConnectionsSavingAndDeletingAndChecking() {
        Assertions.assertFalse(settingService.existAnyDatabaseConnections());

        DatabaseConnection databaseConnection = settingService.saveNewConnection("org.h2.Driver", "org.hibernate.dialect.H2Dialect", "jdbc:h2:", "mem:primary", "TestDatabase", "SA", "Nummer!22");
        Assertions.assertNotNull(databaseConnection);

        Assertions.assertTrue(settingService.existAnyDatabaseConnections());
        settingService.deleteConnection(databaseConnection);

        Assertions.assertFalse(settingService.existAnyDatabaseConnections());
    }

    @Test
    public void getDatabaseConnection() {
        settingService.saveNewConnection("org.h2.Driver", "org.hibernate.dialect.H2Dialect", "jdbc:h2:", "mem:primary", "TestDatabase", "SA", "Nummer!22");
        Assertions.assertTrue(settingService.existAnyDatabaseConnections());

        DatabaseConnection databaseConnection = settingService.getDatabaseConnection("TestDatabase");
        Assertions.assertNotNull(databaseConnection);
    }

    @Test
    public void getAllDatabaseById() {
        ArrayList<DatabaseConnection> databaseConnections = new ArrayList<>();
        DatabaseConnection targetDatabaseConnection = settingService.findCurrentDatabaseConnection("secondOne", databaseConnections);
        Assertions.assertNull(targetDatabaseConnection);

        DatabaseConnection firstDatabaseConnection = new DatabaseConnection();
        DatabaseConnection secondDatabaseConnection = new DatabaseConnection();
        secondDatabaseConnection.setId("secondOne");
        DatabaseConnection thirdDatabaseConnection = new DatabaseConnection();
        thirdDatabaseConnection.setId("thirdOne");
        DatabaseConnection fourthDatabaseConnection = new DatabaseConnection();
        firstDatabaseConnection.setId("fourthOne");

        databaseConnections.add(firstDatabaseConnection);
        databaseConnections.add(thirdDatabaseConnection);
        databaseConnections.add(fourthDatabaseConnection);

        targetDatabaseConnection = settingService.findCurrentDatabaseConnection("secondOne", databaseConnections);
        Assertions.assertNull(targetDatabaseConnection);

        databaseConnections.add(secondDatabaseConnection);

        targetDatabaseConnection = settingService.findCurrentDatabaseConnection("secondOne", databaseConnections);
        Assertions.assertSame(secondDatabaseConnection, targetDatabaseConnection);
    }
}
