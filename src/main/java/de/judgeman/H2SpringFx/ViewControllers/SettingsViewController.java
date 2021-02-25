package de.judgeman.H2SpringFx.ViewControllers;

import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.LogService;
import de.judgeman.H2SpringFx.Services.SettingService;
import de.judgeman.H2SpringFx.Services.ViewService;
import de.judgeman.H2SpringFx.ViewControllers.Abstract.BaseViewController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by Paul Richter on Sun 30/03/2020
 */
@Component
public class SettingsViewController extends BaseViewController {

    private final Logger logger = LogService.getLogger(this.getClass());

    @Autowired
    private SettingService settingService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private ViewService viewService;

    @FXML
    private ComboBox<Locale> languageComboBox;
    @FXML
    private Label titleLabel;
    @FXML
    private Label settingLabel;
    @FXML
    private Label setLanguageLabel;

    @FXML
    private void initialize() {
        initLanguageComboBox();
    }

    private void selectLastUsedLanguage() {
        Locale lastUsedLanguage = languageService.getLastUsedOrDefaultLanguage();
        languageComboBox.setValue(lastUsedLanguage);
    }

    private void initLanguageComboBox() {
        languageComboBox.setItems(FXCollections.observableArrayList(languageService.getAvailableLanguages()));
    }

    @FXML
    private void setNewLanguage() {
        Locale newLanguage = languageComboBox.getValue();

        languageService.setNewLanguage(newLanguage);
        logger.info(String.format("Set new language: %s", newLanguage));
        setNewTextValuesForAllLabels();

        languageService.saveNewLanguageSetting(newLanguage);

        settingLabel.setText(String.format(languageService.getLocalizationText("setNewLanguage"), newLanguage));
    }

    @FXML
    private void backButtonClicked() {
        viewService.getMainViewController().showViewBefore();
    }

    private void setNewTextValuesForAllLabels() {
        titleLabel.setText(languageService.getLocalizationText("settingView.title"));
        setLanguageLabel.setText(languageService.getLocalizationText("setLanguage"));

        logger.info("Updated text properties with new localization");
    }

    @Override
    public void afterViewIsInitialized() {
        selectLastUsedLanguage();
    }
}
