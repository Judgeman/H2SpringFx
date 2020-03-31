package de.judgeman.H2SpringFx.Services;

import de.judgeman.H2SpringFx.Model.SettingEntry;
import de.judgeman.H2SpringFx.Repositories.SettingEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Paul Richter on Mon 30/03/2020
 */
@Service
public class SettingService {

    @Autowired
    private SettingEntryRepository settingEntryRepository;

    public void SaveSetting(String key, String value) {
        SettingEntry settingEntry = settingEntryRepository.findById(key).orElse(null);

        if(settingEntry == null) {
            settingEntry = new SettingEntry();
            settingEntry.setKey(key);
        }

        settingEntry.setValue(value);
        settingEntryRepository.save(settingEntry);
    }

    public String LoadSetting(String key) {
        SettingEntry settingEntry = settingEntryRepository.findById(key).orElse(null);

        if(settingEntry == null) {
            return null;
        }

        return settingEntry.getValue();
    }
}
