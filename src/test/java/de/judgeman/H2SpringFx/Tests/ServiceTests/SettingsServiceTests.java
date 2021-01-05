package de.judgeman.H2SpringFx.Tests.ServiceTests;

import de.judgeman.H2SpringFx.Services.SettingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

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
}
