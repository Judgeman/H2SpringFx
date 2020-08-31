package de.judgeman.H2SpringFx.Controller;

import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.LogService;
import de.judgeman.H2SpringFx.Services.SettingService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Locale;

/**
 * Created by Paul Richter on Sun 30/03/2020
 */
@Component
public class EntryPointViewController {

    private Logger logger = LogService.getLogger(this.getClass());

    @Autowired
    private SettingService settingService;

    @Autowired
    private LanguageService languageService;

    public AnchorPane anchorPane;
    public ComboBox<Locale> languageComboBox;
    public Label titleLabel;
    public Label settingLabel;
    public Label setLanguageLabel;
    public TextField valueInput;
    public Button saveButton;
    public Button loadButton;

    @FXML
    public void initialize() {
        initLanguageComboBox();
        selectLastUsedLanguage();
    }

    private void selectLastUsedLanguage() {
        Locale lastUsedLanguage = languageService.getLastUsedOrDefaultLanguage();
        languageComboBox.setValue(lastUsedLanguage);
    }

    private void initLanguageComboBox() {
        languageComboBox.setItems(FXCollections.observableArrayList(languageService.getAvailableLanguages()));
    }

    public void ShowContextMenuOnEmptySpace(ContextMenuEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setImpl_showRelativeToWindow(true);

        MenuItem item1 = new MenuItem("Menu Item 1");
        contextMenu.getItems().add(item1);

        contextMenu.show(anchorPane, event.getScreenX(), event.getScreenY());
    }

    public void saveValue() {
        String value = valueInput.getText();

        settingService.SaveSetting("TestSettingEntry", value);
        settingLabel.setText(String.format(languageService.getLocalizationText("savedValue"), value));

        logger.info(String.format("Saved value: %s", value));
    }

    public void loadSavedValue() {
        String testSettingValue = settingService.LoadSetting("TestSettingEntry");
        logger.info(String.format("Loaded value: %s", testSettingValue));

        settingLabel.setText(String.format(languageService.getLocalizationText("loadedValue"), testSettingValue));
        valueInput.setText(testSettingValue);
    }

    public void setNewLanguage() {
        Locale newLanguage = languageComboBox.getValue();

        languageService.setNewLanguage(newLanguage);
        logger.info(String.format("Set new language: %s", newLanguage));
        setNewTextValuesForAllLabels();

        languageService.saveNewLanguageSetting(newLanguage);

        settingLabel.setText(String.format(languageService.getLocalizationText("setNewLanguage"), newLanguage));
    }

    private void setNewTextValuesForAllLabels() {
        titleLabel.setText(languageService.getLocalizationText("overviewTitle"));
        saveButton.setText(languageService.getLocalizationText("saveValue"));
        loadButton.setText(languageService.getLocalizationText("loadSavedValue"));
        setLanguageLabel.setText(languageService.getLocalizationText("setLanguage"));

        logger.info("Updated text properties with new localization");
    }
}
