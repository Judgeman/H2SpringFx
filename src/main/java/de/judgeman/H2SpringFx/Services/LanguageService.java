package de.judgeman.H2SpringFx.Services;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class LanguageService {

    public final static Locale defaultLanguage = Locale.GERMANY;
    private final String LOCALIZATION_BUNDLE_NAME = "localization";

    @Autowired
    private SettingService settingService;

    private FXMLLoader fxmlLoader;

    private final ArrayList<Locale> languages;

    public LanguageService() {
        languages = createAvailableLanguageList();
    }

    public ArrayList getAvailableLanguages() {
        return languages;
    }

    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setNewLanguage(Locale locale) {
        checkForRegisteredFXMLLoader();
        setNewResourceBundle(getLocalizationResourceBundle(locale));
    }

    public String getLocalizationText(String key) {
        return fxmlLoader.getResources().getString(key);
    }

    public void registerFXMLLoader(FXMLLoader fxmlLoaderToRegister) {
        fxmlLoader = fxmlLoaderToRegister;
    }

    public void restoreLastUsedOrDefaultResourceBundle() {
        checkForRegisteredFXMLLoader();
        fxmlLoader.setResources(getLocalizationResourceBundle(getLastUsedOrDefaultLanguage()));
    }

    public Locale getLastUsedOrDefaultLanguage() {
        String language = settingService.LoadSetting(SettingService.LANGUAGE_ENTRY_KEY);
        if (language == null) {
            return getDefaultLanguage();
        }

        Locale locale = Locale.forLanguageTag(language);
        if (locale.getLanguage().isEmpty()) {
            return getDefaultLanguage();
        }

        return locale;
    }

    public void saveNewLanguageSetting(Locale newLanguage) {
        settingService.SaveSetting(SettingService.LANGUAGE_ENTRY_KEY, newLanguage.toLanguageTag());
    }

    private ArrayList<Locale> createAvailableLanguageList() {
        ArrayList<Locale> languages = new ArrayList<>();

        languages.add(Locale.GERMANY);
        languages.add(Locale.US);

        return languages;
    }

    private void setNewResourceBundle(ResourceBundle newResourceBundle) {
        fxmlLoader.setResources(newResourceBundle);
    }

    private void checkForRegisteredFXMLLoader() {
        if (fxmlLoader == null) {
            throw new UnsupportedOperationException("FXMLLoader must be register first!");
        }
    }

    private ResourceBundle getLocalizationResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(LOCALIZATION_BUNDLE_NAME, locale);
    }
}
